package com.abi.whatstrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Table1Db extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Table1.Table1Entry.TABLE_NAME + " (" +
                    Table1.Table1Entry._ID + " INTEGER PRIMARY KEY," +
                    Table1.Table1Entry.COLUMN_NAME_TITLE + " TEXT,"+
                    Table1.Table1Entry.COLUMN_CONTACT + " TEXT," +
                    Table1.Table1Entry.COLUMN_CHATS + " TEXT,"+
                    Table1.Table1Entry.COLUMN_TIME + " TEXT," +
                    Table1.Table1Entry.COLUMN_IMAGE + " BLOB ) ";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Table1.Table1Entry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "table1.db";

    public Table1Db(Context context) {
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
