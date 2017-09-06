package com.demo.lizejun.rxsample.chapter14;

import android.util.Log;

import com.demo.lizejun.rxsample.utils.Store;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class TokenLoader {

    private static final String TAG = TokenLoader.class.getSimpleName();

    private AtomicInteger mRefreshing = new AtomicInteger(1);
    private PublishSubject<String> mPublishSubject;

    private TokenLoader() {
        mPublishSubject = PublishSubject.create();
    }

    public static TokenLoader getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final TokenLoader INSTANCE = new TokenLoader();
    }

    public String getCacheToken() {
        return Store.getInstance().getToken();
    }

    public Observable<String> getNetTokenLocked() {
        if (mRefreshing.get() == 1) {
            Log.d(TAG, "没有请求，发起一次新的Token请求");
            mRefreshing.getAndDecrement();
            startTokenRequest();
        } else {
            Log.d(TAG, "已经有请求，直接返回等待");
        }
        return mPublishSubject;
    }

    private void startTokenRequest() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Thread.sleep(1000);
                Log.d(TAG, "发送Token");
                e.onNext(String.valueOf(System.currentTimeMillis()));
            }
        }).subscribeOn(Schedulers.io()).subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String value) {
                Log.d(TAG, "返回Token结果=" + value);
                mRefreshing.getAndIncrement();
                Store.getInstance().setToken(value);
                mPublishSubject.onNext(value);
            }
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "返回Token错误");
                mRefreshing.getAndIncrement();
                mPublishSubject.onError(e);
            }
            @Override
            public void onComplete() {
                Log.d(TAG, "返回Token完成");
                mRefreshing.getAndIncrement();
                mPublishSubject.onComplete();
            }
        });
    }

}
