package com.abi.whatstrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SampleDb extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SampleTable.SampleTableEntry.TABLE_NAME + " (" +
                    SampleTable.SampleTableEntry._ID + " INTEGER PRIMARY KEY," +
                    SampleTable.SampleTableEntry.COLUMN_NAME_TITLE + " TEXT UNIQUE )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SampleTable.SampleTableEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sample.db";

    public SampleDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
