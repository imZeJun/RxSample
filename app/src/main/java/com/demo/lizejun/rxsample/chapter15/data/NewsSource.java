package com.demo.lizejun.rxsample.chapter15.data;

import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import io.reactivex.Observable;

public interface NewsSource {

    Observable<NewsEntity> getNews(String category);

    void saveNews(NewsEntity newsEntity);
}
