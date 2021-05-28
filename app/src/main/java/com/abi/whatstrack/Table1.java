package com.abi.whatstrack;

import android.provider.BaseColumns;

public final class Table1 {

    private Table1() {}

    public static class Table1Entry implements BaseColumns {
        public static final String TABLE_NAME = "Table1";
        public static final String COLUMN_NAME_TITLE = "appname";
        public static final String COLUMN_CONTACT = "contact";
        public static final String COLUMN_CHATS = "chats";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_IMAGE = "icon";
//        public static final Drawable COLUMN_NAME_DRAW = null;
//        public static final ApplicationInfo COLUMN_NAME_APPINFO = null;
    }
}
