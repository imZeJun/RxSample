package com.demo.lizejun.rxsample.chapter15.data;

import com.demo.lizejun.rxsample.chapter15.data.local.LocalNewsSource;
import com.demo.lizejun.rxsample.chapter15.data.remote.RemoteNewsSource;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import io.reactivex.Observable;

public class NewsRepository implements NewsSource {

    private NewsSource mLocalNewsSource;
    private NewsSource mRemoteNewsSource;

    private NewsRepository() {
        mLocalNewsSource = new LocalNewsSource();
        mRemoteNewsSource = new RemoteNewsSource();
    }

    private static class Holder {
        private static NewsRepository INSTANCE = new NewsRepository();
    }

    @Override
    public Observable<NewsEntity> getNews(String category) {
        return null;
    }
}
