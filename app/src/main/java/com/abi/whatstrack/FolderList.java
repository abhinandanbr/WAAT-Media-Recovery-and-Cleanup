package com.abi.whatstrack;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by abhi on 29/3/18.
 */

public class FolderList {

    public FolderList(File folder,String folderName,Drawable drawings,long folderSize) {
        this.folder = folder;
        this.folderName = folderName;
        this.appDrawableDraw = drawings;
        this.folderSize=folderSize;
    }

    public File folder(){
        return folder;
    }

    public long folderSize(){
        return folderSize;
    }

    public Drawable appDrawable(){
        return appDrawableDraw;
    }

    public String folderName(){
        return folderName;
    }

    public File folder;
    public String folderName;
    public Drawable appDrawableDraw;
    public long folderSize;
}