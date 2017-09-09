package com.demo.lizejun.rxsample.chapter15.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.lizejun.rxsample.chapter15.data.NewsContract;

public class NewsDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "rx_mvp.db";
    private static final int DB_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NewsContract.NewsTable.TABLE_NAME + " (" +
                    NewsContract.NewsTable._ID + TEXT_TYPE + " PRIMARY KEY," +
                    NewsContract.NewsTable.COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    NewsContract.NewsTable.COLUMN_NAME_DATA + TEXT_TYPE +
                    " )";

    public NewsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
