package com.abi.whatstrack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.abi.whatstrack.Util.TAG;
import static com.abi.whatstrack.Util.broadcast_String;

public class NotiListenerService extends NotificationListenerService {
    List<String> listTime = new ArrayList<>();
    List<String> listID = new ArrayList<>();


    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int WHATSAPP_CODE = 2;
        public static final int INSTAGRAM_CODE = 3;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    "example",
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, "example")
                    .setContentTitle("Whats Track")
                    .setContentText("Test")
                    .setSmallIcon(R.mipmap.ic_wc_round)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            stopForeground(true);

        }

        return START_STICKY;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode == InterceptedNotificationCode.WHATSAPP_CODE){

//            if (System.currentTimeMillis() - sbn.getNotification().when > 3000 /*||
//                    isInArray(sbn.getNotification().when)*/) {
//                //when != sbn.getPostTime()
//            } else {
                Log.i(TAG,"Notify Posted");
                //Else, push to the array of notifications "when-id" to compare next time
                updateTable(getApplicationContext(),sbn);
                Intent intent = new  Intent(broadcast_String);
                intent.putExtra("Notification Code", notificationCode);
                sendBroadcast(intent);

//                goNextPosition(sbn.getNotification().when);
//            }


        }
    }


    public void updateTable(Context context,StatusBarNotification sbn){
        String contact=null,message=null,time=null;
        String preContact=null;

        Bundle bundle = sbn.getNotification().extras;
        String summaryText=bundle.getString("android.summaryText");

        getTime(context); //get Time

        Table1Db table1Db = new Table1Db(context);

        boolean isGroup = bundle.getBoolean("android.isGroupConversation");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SQLiteDatabase db = table1Db.getWritableDatabase();
        Bitmap bitmap = (Bitmap) bundle.getParcelable("android.largeIcon");

//        Long time1 = sbn.getPostTime();
//        Long time2 = sbn.getNotification().when;

        int groupLength = 0;

//        String b = (String) bundle.get(Notification.EXTRA_TITLE);
//        if(b!=null) {
//            groupLength = b.length();
//        }

//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
//            String content=null;
//
//            String test=sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();

//            if(b != null){
//                content = "";
//                for (Parcelable tmp : b){
//
//                    Bundle msgBundle = (Bundle) tmp;
//                    content = content + msgBundle.getString("text") + "\n";
//
//                    Set<String> io = msgBundle.keySet(); // To get the keys available for this bundle*/
//
//                }
//            }
//        }

        if(bitmap!=null) {

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

//        context.deleteDatabase(Table1Db.DATABASE_NAME);

            try {
                if(isGroup){
                    //Contact
                    preContact  = bundle.getString("android.title");
                    String[] contactSplit= preContact.split(":");
                    preContact="";
                    for(int i=0;i<contactSplit.length-1;i++){
                        preContact+=contactSplit[i];
                    }

                    //Message
                    String messageSplit=contactSplit[contactSplit.length-1];
                    message = messageSplit+":"+bundle.getString("android.text");


                    //Split for actual title
                    if(preContact.contains("(")) {
                        String[] contactSplit1 = preContact.split("\\(");
                        preContact = "";
                        for (int i = 0; i < contactSplit1.length - 1; i++) {
                            preContact += contactSplit1[i];
                        }
                        if(preContact.charAt(preContact.length() -1)==' ') {
                            String finalContact = preContact.substring(0, preContact.length() - 1).trim();
                            contact = finalContact.trim().replace("\u200E","");
                        }
                    }else{
                        if(preContact.charAt(preContact.length()-1)==' ') {
                            preContact = preContact.substring(0, preContact.length() - 2);
                            contact = preContact.trim().replace("\u200E","");
                        }else{
                            contact = preContact.trim().replace("\u200E","");
                        }
                    }
//                    contactSplit= contact.split("\\(");
//                    if(contactSplit[0]!=null){
//                        for (String string : titleSearch) {
//                            if(string.contains(contactSplit[0])){
//                                contact=string;
//                                break;
//                            }
//                        }
//                    }

                }else {
                   contact  = bundle.getString("android.title").replace("\u200E","");
                   message  = bundle.getString("android.text");
                }


                String sortOrder =
                        Table1.Table1Entry._ID + " DESC";

                Cursor cursor = db.query(Table1.Table1Entry.TABLE_NAME, null, null,
                        null, null, null, sortOrder);

                ContentValues values = new ContentValues();

                time=Long.toString(sbn.getNotification().when);


                if(!listTime.contains(time)) {

                    values.put(Table1.Table1Entry.COLUMN_NAME_TITLE, notificationPackage(sbn));
                    values.put(Table1.Table1Entry.COLUMN_CONTACT, contact);
                    values.put(Table1.Table1Entry.COLUMN_CHATS, message);
                    values.put(Table1.Table1Entry.COLUMN_TIME, time);
                    values.put(Table1.Table1Entry.COLUMN_IMAGE, stream.toByteArray());
                    db.insertOrThrow(Table1.Table1Entry.TABLE_NAME, null, values);
                }
                else{
                    Log.i(TAG, "table123");
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                Log.i(TAG, "table1");
            }
        }
        /*else if(summaryText!=null){
            message = bundle.getString("android.text");


            //Split for integer
            String[] split= message.split(" ");
            split[0].replace("\"", "");
            long checkInt=0;

            try {
                checkInt = Long.parseLong(split[0].toString().replaceAll("\\D+", "").trim());

                    List<String> titleSearch = getTitle(context);

                    contact = bundle.getString("android.title");

                    int count = 0;

                    SQLiteDatabase dbUpdate = table1Db.getWritableDatabase();
                    try {


                        for (String string : titleSearch) {
                            if (string.contains(contact)) {

                                ContentValues values = new ContentValues();
                                values.put(Table1.Table1Entry.COLUMN_CONTACT, contact);

                                String selection = Table1.Table1Entry._ID + " = " + listID.get(count);
                                String[] selectionArgs = {string};

                                dbUpdate.update(
                                        Table1.Table1Entry.TABLE_NAME,
                                        values,
                                        selection,
                                        null);
                            }
                            count++;
                        }
                        dbUpdate.close();
                    } catch (Exception e) {
                        Log.i(TAG, "table1 update");
                    }
            }
            catch(NumberFormatException er)
            {
                Log.i(TAG,"string check"+checkInt);
            }
        }*/

        listTime.clear();

    }

    public List<String> getTitle(Context context){
        Table1Db table1Db = new Table1Db(context);
        List<String> listContact = new ArrayList<>();
        SQLiteDatabase db = table1Db.getReadableDatabase();
        try {
            String sortOrder =
                    Table1.Table1Entry._ID + " DESC";

            Cursor cursor = db.query(Table1.Table1Entry.TABLE_NAME, null, null,
                    null, null, null, sortOrder);

            while (cursor.moveToNext()) {
                String contactNm = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_CONTACT));
                String ID = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry._ID));
//                String timeNm = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_TIME));
//                    byte[] drawable = cursor.getBlob(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_IMAGE));
//                    Bitmap  bitmap = BitmapFactory.decodeByteArray(drawable, 0, drawable.length);
//                    Log.i(TAG, "DABASE " + item+" "+itemId);

                listContact.add(contactNm);
                listID.add(ID);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.i(TAG, "table1 title");
        }

        return listContact;
    }

    public void getTime(Context context){
        Table1Db table1Db = new Table1Db(context);
        SQLiteDatabase db = table1Db.getReadableDatabase();
        try {
            String sortOrder =
                    Table1.Table1Entry._ID + " DESC";

            Cursor cursor = db.query(Table1.Table1Entry.TABLE_NAME, null, null,
                    null, null, null, sortOrder);

            while (cursor.moveToNext()) {
                String timeNm = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_TIME));
                listTime.add(timeNm);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.i(TAG, "table1 title");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

//        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
//
//            StatusBarNotification[] activeNotifications = this.getActiveNotifications();
//
//            if(activeNotifications != null && activeNotifications.length > 0) {
//                for (int i = 0; i < activeNotifications.length; i++) {
//                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
//                        Intent intent = new Intent(broadcast_String);
//                        intent.putExtra("Notification Code", notificationCode);
//                        sendBroadcast(intent);
//                        break;
//                    }
//                }
//            }
//        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }

    private String notificationPackage(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return sbn.getPackageName();
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(ApplicationPackageNames.INSTAGRAM_PACK_NAME);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(ApplicationPackageNames.WHATSAPP_PACK_NAME);
        }else{
            return "NONE";
        }
    }
}
