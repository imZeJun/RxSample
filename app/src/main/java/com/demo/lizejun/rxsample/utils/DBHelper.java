package com.demo.lizejun.rxsample.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final String DB_NAME = "table.db";
    private static final int DB_VERSION = 1;

    public static final String FIRST_TABLE_NAME = "first_table";
    public static final String COLUMN1_NAME = "column1";

    private static final String CREATE_FIRST_TABLE = "create table if not exists first_table ("
            + "id integer primary key,"
            + "column1 integer)";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_FIRST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
