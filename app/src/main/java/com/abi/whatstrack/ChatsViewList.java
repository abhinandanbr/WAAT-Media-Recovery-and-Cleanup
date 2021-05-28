package com.abi.whatstrack;

import android.graphics.Bitmap;

/**
 * Created by abhi on 29/3/18.
 */

public class ChatsViewList {

    public ChatsViewList(String message, String time, Bitmap drawings) {
        this.message = message;
        this.time = time;
        this.appDrawableDraw = drawings;
//        this.folderSize=folderSize;
    }

    public String getchatMessage(){
        return message;
    }

//    public long folderSize(){
//        return folderSize;
//    }
//
    public Bitmap appDrawable(){
        return appDrawableDraw;
    }

    public String gettime(){
        return time;
    }

    public String message;
    public String time;
    public Bitmap appDrawableDraw;
//    public long folderSize;
}