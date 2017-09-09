package com.demo.lizejun.rxsample.chapter15.news;


import com.demo.lizejun.rxsample.chapter15.data.NewsRepository;
import com.demo.lizejun.rxsample.chapter15.data.bean.NewsBean;
import com.demo.lizejun.rxsample.chapter15.news.NewsMvpContract.RefreshType;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import com.demo.lizejun.rxsample.network.entity.NewsResultEntity;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenter implements NewsMvpContract.Presenter {

    private static final long AUTO_REFRESH_TIME = 1000 * 60 * 10;
    private CompositeDisposable mCompositeDisposable;
    private NewsMvpContract.View mView;
    private List<NewsBean> mNewsBeans;
    private long mLastNetUpdateTime;

    public NewsPresenter(NewsMvpContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
        mNewsBeans = new ArrayList<>();
    }

    @Override
    public void refresh(@RefreshType int refreshType) {
        if (refreshType == NewsMvpContract.REFRESH_CACHE) {
            NewsRepository.getInstance()
                    .getCacheNews("Android")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RefreshObserver(refreshType));
        } else {
            if (System.currentTimeMillis() - mLastNetUpdateTime > AUTO_REFRESH_TIME) { //自动刷新的间隔时间为十分钟。
                NewsRepository.getInstance()
                        .getNetNews("Android")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new RefreshObserver(refreshType));
            }
        }
    }

    @Override
    public void destroy() {
        mCompositeDisposable.clear();
        mView = null;
    }

    private void updateNewsBeans(@NewsMvpContract.RefreshType int refreshType, NewsEntity newsEntity) {
        List<NewsBean> filter = new ArrayList<>();
        for (NewsResultEntity resultEntity : newsEntity.getResults()) { //对资讯进行去重，需要重写NewsBean的对应方法。
            NewsBean newsBean = entityToBean(resultEntity);
            if (!mNewsBeans.contains(newsBean)) {
                filter.add(newsBean);
            }
        }
        if (refreshType == NewsMvpContract.REFRESH_CACHE && mNewsBeans.size() == 0) { //只有当前没有数据时，才使用缓存。
            mNewsBeans = filter;
        } else if (refreshType == NewsMvpContract.REFRESH_AUTO) { //自动刷新的数据放在头部。
            mNewsBeans.addAll(0, filter);
            mLastNetUpdateTime = System.currentTimeMillis();
        }
    }

    private NewsBean entityToBean(NewsResultEntity resultEntity) {
        String title = resultEntity.getDesc();
        NewsBean bean = new NewsBean();
        bean.setTitle(title);
        return bean;
    }

    private class RefreshObserver extends DisposableObserver<NewsEntity> {

        private @NewsMvpContract.RefreshType int mRefreshType;

        RefreshObserver(@NewsMvpContract.RefreshType int refreshType) {
            mRefreshType = refreshType;
        }

        @Override
        public void onNext(NewsEntity newsEntity) {
            updateNewsBeans(mRefreshType, newsEntity);
            mView.onRefreshFinished(mRefreshType, mNewsBeans);
        }

        @Override
        public void onError(Throwable throwable) {
            mView.showTips("刷新错误");
        }

        @Override
        public void onComplete() {}
    }
}
