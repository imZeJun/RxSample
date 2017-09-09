package com.demo.lizejun.rxsample.chapter8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.network.entity.NewsAdapter;
import com.demo.lizejun.rxsample.network.entity.NewsResultEntity;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CacheActivity extends AppCompatActivity {

    private static final String TAG = CacheActivity.class.getSimpleName();

    private Button mBtContactRefresh;
    private Button mBtContactEagerRefresh;
    private Button mBtMergeRefresh;
    private Button mBtPublishRefresh;
    private NewsAdapter mNewsAdapter;
    private List<NewsResultEntity> mNewsResultEntities = new ArrayList<>();
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        mBtContactRefresh = (Button) findViewById(R.id.bt_contact_refresh);
        mBtContactEagerRefresh = (Button) findViewById(R.id.bt_contact_eager_refresh);
        mBtMergeRefresh = (Button) findViewById(R.id.bt_merge_refresh);
        mBtPublishRefresh = (Button) findViewById(R.id.bt_publish_refresh);
        mBtContactRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshArticleUseContact();
            }
        });
        mBtContactEagerRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshArticleUseContactEager();
            }
        });
        mBtMergeRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshArticleUseMerge();
            }
        });
        mBtPublishRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshArticleUsePublish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mNewsAdapter = new NewsAdapter(mNewsResultEntities);
        recyclerView.setAdapter(mNewsAdapter);
        mCompositeDisposable = new CompositeDisposable();
    }

    private void refreshArticleUseContact() {
        Observable<List<NewsResultEntity>> contactObservable = Observable.concat(
                getCacheArticle(500).subscribeOn(Schedulers.io()), getNetworkArticle(2000).subscribeOn(Schedulers.io()));
        DisposableObserver<List<NewsResultEntity>> disposableObserver = getArticleObserver();
        contactObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    private void refreshArticleUseContactEager() {
        List<Observable<List<NewsResultEntity>>> observables = new ArrayList<>();
        observables.add(getCacheArticle(2500).subscribeOn(Schedulers.io()));
        observables.add(getNetworkArticle(2000).subscribeOn(Schedulers.io()));
        Observable<List<NewsResultEntity>> contactObservable = Observable.concatEager(observables);
        DisposableObserver<List<NewsResultEntity>> disposableObserver = getArticleObserver();
        contactObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    private void refreshArticleUseMerge() {
        Observable<List<NewsResultEntity>> contactObservable = Observable.merge(
                getCacheArticle(2000).subscribeOn(Schedulers.io()), getNetworkArticle(500).subscribeOn(Schedulers.io()));
        DisposableObserver<List<NewsResultEntity>> disposableObserver = getArticleObserver();
        contactObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    private void refreshArticleUsePublish() {
        Observable<List<NewsResultEntity>> publishObservable = getNetworkArticle(500).subscribeOn(Schedulers.io()).publish(new Function<Observable<List<NewsResultEntity>>, ObservableSource<List<NewsResultEntity>>>() {

            @Override
            public ObservableSource<List<NewsResultEntity>> apply(Observable<List<NewsResultEntity>> network) throws Exception {
                return Observable.merge(network, getCacheArticle(2000).subscribeOn(Schedulers.io()).takeUntil(network));
            }

        });
        DisposableObserver<List<NewsResultEntity>> disposableObserver = getArticleObserver();
        publishObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }


    private Observable<List<NewsResultEntity>> getCacheArticle(final long simulateTime) {
        return Observable.create(new ObservableOnSubscribe<List<NewsResultEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewsResultEntity>> observableEmitter) throws Exception {
                try {
                    Log.d(TAG, "开始加载缓存数据");
                    Thread.sleep(simulateTime);
                    List<NewsResultEntity> results = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        NewsResultEntity entity = new NewsResultEntity();
                        entity.setType("缓存");
                        entity.setDesc("序号=" + i);
                        results.add(entity);
                    }
                    observableEmitter.onNext(results);
                    observableEmitter.onComplete();
                    Log.d(TAG, "结束加载缓存数据");
                } catch (InterruptedException e) {
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onError(e);
                    }
                }
            }
        });
    }

    private Observable<List<NewsResultEntity>> getNetworkArticle(final long simulateTime) {
        return Observable.create(new ObservableOnSubscribe<List<NewsResultEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewsResultEntity>> observableEmitter) throws Exception {
                try {
                    Log.d(TAG, "开始加载网络数据");
                    Thread.sleep(simulateTime);
                    List<NewsResultEntity> results = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        NewsResultEntity entity = new NewsResultEntity();
                        entity.setType("网络");
                        entity.setDesc("序号=" + i);
                        results.add(entity);
                    }
                    //a.正常情况。
                    observableEmitter.onNext(results);
                    observableEmitter.onComplete();
                    //b.发生异常。
                    //observableEmitter.onError(new Throwable("netWork Error"));
                    Log.d(TAG, "结束加载网络数据");
                } catch (InterruptedException e) {
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onError(e);
                    }
                }
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends List<NewsResultEntity>>>() {

            @Override
            public ObservableSource<? extends List<NewsResultEntity>> apply(Throwable throwable) throws Exception {
                Log.d(TAG, "网络请求发生错误throwable=" + throwable);
                return Observable.never();
            }
        });
    }

    private DisposableObserver<List<NewsResultEntity>> getArticleObserver() {
        return new DisposableObserver<List<NewsResultEntity>>() {

            @Override
            public void onNext(List<NewsResultEntity> newsResultEntities) {
                mNewsResultEntities.clear();
                mNewsResultEntities.addAll(newsResultEntities);
                mNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "加载错误, e=" + throwable);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "加载完成");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
