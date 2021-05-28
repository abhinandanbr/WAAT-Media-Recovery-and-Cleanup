package com.abi.whatstrack;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by abhi on 29/3/18.
 */

public class ChatList {

    public ChatList(String chatApp, String contact,Bitmap drawings) {
        this.chatApp = chatApp;
        this.contact = contact;
        this.appDrawableDraw = drawings;
//        this.folderSize=folderSize;
    }

    public String getchatApp(){
        return chatApp;
    }

//    public long folderSize(){
//        return folderSize;
//    }
//
    public Bitmap appDrawable(){
        return appDrawableDraw;
    }

    public String getContact(){
        return contact;
    }

    public String chatApp;
    public String contact;
    public Bitmap appDrawableDraw;
//    public long folderSize;
}