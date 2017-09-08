package com.demo.lizejun.rxsample.chapter15.news;


import com.demo.lizejun.rxsample.chapter15.data.NewsRepository;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenter implements NewsMvpContract.Presenter {

    private CompositeDisposable mCompositeDisposable;
    private NewsMvpContract.View mView;

    public NewsPresenter(NewsMvpContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void dispose() {
        mCompositeDisposable.clear();
        mView = null;
    }

    @Override
    public void loadNews(boolean loadCache) {
        mView.showLoadingIndicator(true);
        Observable<NewsEntity> newsEntity = NewsRepository.getInstance().getNews("Android");
        DisposableObserver<NewsEntity> observer = new DisposableObserver<NewsEntity>() {

            @Override
            public void onNext(NewsEntity value) {
                mView.showNews(value);
                mView.showLoadingIndicator(false);
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        };
        newsEntity.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }
}
