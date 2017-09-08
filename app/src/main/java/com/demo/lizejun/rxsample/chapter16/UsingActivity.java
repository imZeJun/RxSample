package com.demo.lizejun.rxsample.chapter16;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.utils.DBHelper;
import com.demo.lizejun.rxsample.utils.Utils;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class UsingActivity extends AppCompatActivity {

    private static final String TAG = UsingActivity.class.getSimpleName();
    private static final String ERROR_NO_RESULT = "no_result";

    private Button mBtnInsert;
    private Button mBtnQuery;
    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);
        mBtnInsert = (Button) findViewById(R.id.bt_insert);
        mBtnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInsert();
            }
        });
        mBtnQuery = (Button) findViewById(R.id.bt_query);
        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQuery();
            }
        });
        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    private void doInsert() {
        Function<SQLiteDatabase, Observable<Long>> iOperation = new Function<SQLiteDatabase, Observable<Long>>() {
            @Override
            public Observable<Long> apply(SQLiteDatabase sqLiteDatabase) throws Exception {
                Log.d(TAG, "插入线程=" + Thread.currentThread().getId());
                Thread.sleep(10000);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.COLUMN1_NAME, (int) (Math.random() * 100));
                long id = sqLiteDatabase.insert(DBHelper.FIRST_TABLE_NAME, null, contentValues);
                return Observable.just(id);
            }
        };
        getSQLiteDataBase(iOperation).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "插入一个随机数据=" + aLong);
            }
        });
    }

    private void doQuery() {
        Function<SQLiteDatabase, Observable<String>> qOperation = new Function<SQLiteDatabase, Observable<String>>() {
            @Override
            public Observable<String> apply(SQLiteDatabase sqLiteDatabase) throws Exception {
                Log.d(TAG, "查询线程=" + Thread.currentThread().getId());
                Cursor cursor = sqLiteDatabase.query(DBHelper.FIRST_TABLE_NAME, null, null, null, null, null, null);
                String result = "";
                boolean success = false;
                while (cursor != null && cursor.moveToNext()) {
                    success = true;
                    int value = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN1_NAME));
                    result = result + value + ",";
                }
                if (cursor != null) {
                    cursor.close();
                }
                Observable<String> observable;
                if (success) {
                    observable = Observable.just(result);
                } else {
                    observable = Observable.error(new Throwable(ERROR_NO_RESULT));
                }
                return observable;
            }
        };
        getSQLiteDataBase(qOperation).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String value) throws Exception {
                Log.d(TAG, "查询结果=" + value);
                mTvResult.setText("查询结果=" + value);
            }
        });
    }

    private <T> Observable<T> getSQLiteDataBase(Function<SQLiteDatabase, Observable<T>> operation) {
        return Observable.using(new Callable<SQLiteDatabase>() {
            @Override
            public SQLiteDatabase call() throws Exception {
                Log.d(TAG, "打开数据库线程=" + Thread.currentThread().getId());
                DBHelper helper = new DBHelper(Utils.getAppContext());
                helper.close();
                return helper.getWritableDatabase();
            }
        }, operation, new Consumer<SQLiteDatabase>() {
            @Override
            public void accept(SQLiteDatabase sqLiteDatabase) throws Exception {
                Log.d(TAG, "关闭数据库线程=" + Thread.currentThread().getId());
                sqLiteDatabase.close();
            }
        });
    }
}
