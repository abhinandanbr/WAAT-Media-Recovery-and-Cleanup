package com.abi.whatstrack;


import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



import java.util.ArrayList;

public class Notification {

    private  final String Notify="com.abhi.whatstrack.NOTIFY";
    private Runnable runnable;
    private static final String TAG =Util.TAG;
    private static final String ANDROID_PRE_BASE = "pre_base";
    private boolean gpsFalseCheck=false;
    private  Thread thread=null;
    private  SQLiteDatabase db;
//    private  AppListDb appListDb;
    private  ContentValues values;
    private ArrayList<String> appUsageIds;
    private String topPackageName="" ;


    public Notification(@NonNull Context context, @NonNull String channelId) {

    }

    public Notification(Context context) {
        this(context, null);
    }

    public Notification showNotification(Context context, boolean bool,
                                 int ID, int DrawableSmall, boolean persistant,
                                         String Title, String Text) {

        android.app.Notification n;
        if (bool) {
            Intent intent1 = new Intent(context, Notification_Handler.class);
            intent1.putExtra("ID", ID);
            PendingIntent pIntent = PendingIntent.getActivity(context, ID, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//            Log.i(TAG,"RECENTS: "+pIntent);
//            boolean b= isNotificationVisible(context,ID);
            if(Build.VERSION.SDK_INT >= 26){
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationChannel channel = new NotificationChannel("WhatsTrack","Whats Track",NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(null,null);

                mNotificationManager.createNotificationChannel(channel);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context,"WhatsTrack")
                                .setSmallIcon(DrawableSmall)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_wc_round))
                                .setColor(context.getResources().getColor(R.color.colorAccent))
                                .setContentTitle(Title)
                                .setContentIntent(pIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setOngoing(true)
                                .setContentText(Text);

                if(Title.contains("Wi-Fi")) {
//                    wifiCheckThread(context, 5000);
                }

                if(Title.contains("GPS")) {
                    if(Build.VERSION.SDK_INT<21) {
//                        appListCheckLow(context, 10000, true);
                    }else if(Build.VERSION.SDK_INT>=21){
//                        appListCheckHigh(context, 10000, true);
                    }
                }

                if (persistant)
                    mBuilder.setOngoing(true);
                else
                    mBuilder.setOngoing(false);

                mNotificationManager.notify(ID, mBuilder.build());

            }
            else if (Build.VERSION.SDK_INT <= 19) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(DrawableSmall)
                                .setColor(context.getResources().getColor(R.color.colorAccent))
                                .setContentTitle(Title)
                                .setContentIntent(pIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentText(Text);
                    n = mBuilder.build();

                if(Title.contains("Wi-Fi")) {
//                    wifiCheckThread(context, 5000);
                }
                if(Title.contains("GPS")) {
                    if(Build.VERSION.SDK_INT<21) {
//                        appListCheckLow(context, 10000, true);
                    }else if(Build.VERSION.SDK_INT>=21){
//                        appListCheckHigh(context, 10000, true);
                    }
                }


                if (persistant)
                    n.flags |= android.app.Notification.FLAG_NO_CLEAR | android.app.Notification.FLAG_ONGOING_EVENT;

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                StatusBarNotification[] statusBarNotifications = mNotificationManager.getActiveNotifications();
//                Log.i(TAG,"RECENTS: "+statusBarNotifications.length);
                mNotificationManager.notify(ID, n);
            } else {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(DrawableSmall)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_wc_round))
                                .setColor(context.getResources().getColor(R.color.colorAccent))
                                .setContentTitle(Title)
                                .setContentIntent(pIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentText(Text);

                    n = mBuilder.getNotification();

                if(Title.contains("Wi-Fi")) {
//                    wifiCheckThread(context, 5000);
                }
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (persistant)
                    n.flags |= android.app.Notification.FLAG_NO_CLEAR | android.app.Notification.FLAG_ONGOING_EVENT;

                mNotificationManager.notify(ID, n);
            }

        } else {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(ID);
        }
        return this;
    }

    @TargetApi(23)
    private boolean isNotificationVisible(Context context,int MY_ID) {

        NotificationManager notificationManager =(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();
        if(statusBarNotifications!=null) {
            for (int i = 0; i < statusBarNotifications.length; i++) {
                Log.i(TAG, "RECENT ON:" + statusBarNotifications[i].getId());
                if (statusBarNotifications[i].getId() == MY_ID) {
                    return true;
                }
            }
        }
        return false;
    }

}
