package com.demo.lizejun.rxsample.chapter15.data;

import android.provider.BaseColumns;

public class NewsContract {

    private NewsContract() {}

    public static abstract class NewsTable implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NAME_DATA = "data";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }
}
