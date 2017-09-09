package com.demo.lizejun.rxsample.chapter15.data;

import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class NewsRepository {

    private LocalNewsSource mLocalNewsSource;
    private RemoteNewsSource mRemoteNewsSource;

    private NewsRepository() {
        mLocalNewsSource = new LocalNewsSource();
        mRemoteNewsSource = new RemoteNewsSource();
    }

    public static NewsRepository getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static NewsRepository INSTANCE = new NewsRepository();
    }

    public Observable<NewsEntity> getNetNews(String category) {
        return mRemoteNewsSource.getNews(category).doOnNext(new Consumer<NewsEntity>() {
            @Override
            public void accept(NewsEntity newsEntity) throws Exception {
                mLocalNewsSource.saveNews(newsEntity);
            }
        });
    }

    public Observable<NewsEntity> getCacheNews(String category) {
        return mLocalNewsSource.getNews(category);
    }

}
