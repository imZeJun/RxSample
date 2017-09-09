package com.demo.lizejun.rxsample.chapter15.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import com.demo.lizejun.rxsample.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LocalNewsSource {

    private static final String[] QUERY_PROJECTION = new String[] { NewsContract.NewsTable.COLUMN_NAME_DATA };
    private static final String QUERY_SELECTION = NewsContract.NewsTable.COLUMN_NAME_CATEGORY + "= ?";

    private NewsDBHelper mNewsDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public LocalNewsSource() {
        mNewsDBHelper = new NewsDBHelper(Utils.getAppContext());
        mSQLiteDatabase = mNewsDBHelper.getWritableDatabase();
    }

    public Observable<NewsEntity> getNews(String category) {
        return Observable.just(category).flatMap(new Function<String, ObservableSource<NewsEntity>>() {
            @Override
            public ObservableSource<NewsEntity> apply(String category) throws Exception {
                NewsEntity newsEntity = new NewsEntity();
                Cursor cursor = mSQLiteDatabase.query(NewsContract.NewsTable.TABLE_NAME, QUERY_PROJECTION, QUERY_SELECTION, new String[] { category }, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    String data = cursor.getString(cursor.getColumnIndex(NewsContract.NewsTable.COLUMN_NAME_DATA));
                    newsEntity = JSON.parseObject(data, NewsEntity.class);
                }
                if (cursor != null) {
                    cursor.close();
                }
                return Observable.just(newsEntity);
            }
        });
    }


    public void saveNews(NewsEntity newsEntity) {
        Observable.just(newsEntity).observeOn(Schedulers.io()).subscribe(new Consumer<NewsEntity>() {

            @Override
            public void accept(NewsEntity newsEntity) throws Exception {
                if (newsEntity.getResults() != null && newsEntity.getResults().size() > 0) {
                    String cache = JSON.toJSONString(newsEntity);
                    ContentValues values = new ContentValues();
                    values.put(NewsContract.NewsTable.COLUMN_NAME_CATEGORY, "Android");
                    values.put(NewsContract.NewsTable.COLUMN_NAME_DATA, cache);
                    mSQLiteDatabase.insert(NewsContract.NewsTable.TABLE_NAME, null, values);
                }
            }
        });
    }
}
