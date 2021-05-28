package com.abi.whatstrack;

import android.os.FileObserver;
import android.util.Log;

import java.io.File;

/**
 * Created by AbhinandanBR on 014 14/5/2017.
 */

public class FileObserve extends FileObserver {

    String TAG =Util.TAG;
    File Directory;

    public FileObserve(String path) {
        this(path, ALL_EVENTS);
    }

    public FileObserve(String path, int mask) {
        super(path, mask);
    }

    @Override
    public void onEvent(int i, String s) {
        Log.i(TAG,"Event"+s+" "+i);

    }
}
