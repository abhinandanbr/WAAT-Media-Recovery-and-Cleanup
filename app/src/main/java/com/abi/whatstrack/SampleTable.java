package com.abi.whatstrack;

import android.provider.BaseColumns;

public final class SampleTable {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SampleTable() {}

    /* Inner class that defines the table contents */
    public static class SampleTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "sample";
        public static final String COLUMN_NAME_TITLE = "title_for_life";
        //public static final Drawable COLUMN_NAME_DRAW = null;
        //public static final ApplicationInfo COLUMN_NAME_APPINFO = null;
    }
}
