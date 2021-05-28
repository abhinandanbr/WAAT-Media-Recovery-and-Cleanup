package com.abi.whatstrack;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;

public class LowMem extends Service {

    Handler handler;
    private FileOutputStream fos, fos1;
    File Directory;
    private boolean first = true;
    int no=0;
    long filetime=0;


    long fileArraytimeMax=0;
    ArrayList<File> fileArrayList= new ArrayList<File>();
    ArrayList<Long> fileArraytime= new ArrayList<Long>();
    String TAG =Util.TAG;
    private FileObserver fileObserver;
    int filecount=0;
    long temp=0;

    NotificationManager mNotificationManager;
    Timer timer;
    com.abi.whatstrack.Notification notification;

    public LowMem() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        timer = new Timer();

        mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notification=new com.abi.whatstrack.Notification(getApplicationContext());
        Log.i(TAG,"Start");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//            @Override
//            public void run() {
//                Thread Scan = new Thread() {
//                    public void run() {
                        Log.i(TAG,"Started");
                        fileObserver = new FileObserve(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Databases",FileObserve.MOVED_TO){
                            public void onEvent(int i, String s){
                                Log.i(TAG,"Event: "+s+" "+i);
                                try {
                                    filetime=0;
                                    fileArraytimeMax=0;
                                    fileArrayList.clear();
                                    fileArraytime.clear();
                                    Directory = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Databases/");
                                    for (File file : Directory.listFiles()) {

                                        fileArrayList.add(file);
                                        fileArraytime.add(file.lastModified());
                                        fileArraytimeMax=Math.max(fileArraytimeMax,file.lastModified());

                                        no++;

                                    }
                                    if(no>1) {

                                        for (int j = 0; j < fileArrayList.size(); j++) {
                                            filetime = fileArraytime.get(j);

//                                            FileUtils.copyFileToDirectory(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Databases/"+fileArrayList.get(j).getName(),
//                                                    Environment.getExternalStorageDirectory().getPath()+"/test/");

                                            if (filetime != fileArraytimeMax && no > 1) {
                                                fileArrayList.get(j).delete();
                                            }
                                        }
                                        showNotification(getApplicationContext(),true);
                                    }
                                    else{
                                        //fileObserver.stopWatching();
                                        Log.i(TAG,"No Changes Made");
                                    }


                                    Log.i(TAG,""+no);
                                    no=0;
                                } catch (Exception e) {

                                }
                            }
                        };
                        fileObserver.startWatching();
//                };

//        Scan.start();

        return START_STICKY;
    }

    private void showNotification(Context context, boolean bool) {
//        inAppPurchaseSetup(context);

        if(bool) {
            notification.showNotification(context,bool,1,
                    R.mipmap.ic_wc_round,false,"File:."+no,"WhatsTrack Scan Check");
        }
        else {
            notification.showNotification(context,false,1,
                    R.mipmap.ic_wc_round,false,"File:."+no,"WhatsTrack Scan Check");
        }

    }

}
