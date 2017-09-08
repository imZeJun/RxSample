package com.demo.lizejun.rxsample.chapter15.news;

import com.demo.lizejun.rxsample.chapter15.BasePresenter;
import com.demo.lizejun.rxsample.chapter15.BaseView;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;

public class NewsMvpContract {

    public interface View extends BaseView {

        void showLoadingIndicator(boolean loading);

        void showNews(NewsEntity newsEntity);
    }

    public interface Presenter extends BasePresenter {
        void loadNews(boolean loadCache);
    }

}
