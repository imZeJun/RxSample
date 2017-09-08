package com.demo.lizejun.rxsample.chapter15.data;

import com.demo.lizejun.rxsample.chapter15.data.local.LocalNewsSource;
import com.demo.lizejun.rxsample.chapter15.data.remote.RemoteNewsSource;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class NewsRepository implements NewsSource {

    private NewsSource mLocalNewsSource;
    private NewsSource mRemoteNewsSource;

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

    @Override
    public Observable<NewsEntity> getNews(String category) {
        return mRemoteNewsSource.getNews("Android").publish(new Function<Observable<NewsEntity>, ObservableSource<NewsEntity>>() {
            @Override
            public ObservableSource<NewsEntity> apply(Observable<NewsEntity> newsEntityObservable) throws Exception {
                return Observable.merge(newsEntityObservable, mLocalNewsSource.getNews("Android").takeUntil(newsEntityObservable));
            }
        }).doOnNext(new Consumer<NewsEntity>() {

            @Override
            public void accept(NewsEntity newsEntity) throws Exception {
                mLocalNewsSource.saveNews(newsEntity);
            }
        });
    }

    @Override
    public void saveNews(NewsEntity newsEntity) {}
}
