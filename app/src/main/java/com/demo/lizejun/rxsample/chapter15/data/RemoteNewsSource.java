package com.demo.lizejun.rxsample.chapter15.data;

import com.demo.lizejun.rxsample.network.entity.NewsApi;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteNewsSource {

    private NewsApi mNewsApi;

    public RemoteNewsSource() {
        mNewsApi = new Retrofit.Builder()
                .baseUrl("http://gank.io")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(NewsApi.class);
    }

    public Observable<NewsEntity> getNews(String category) {
        return mNewsApi.getNews(category, 10, 1);
    }

}
