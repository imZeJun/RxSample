package com.demo.lizejun.rxsample.chapter14;

import android.util.Log;
import com.demo.lizejun.rxsample.utils.Store;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class TokenLoader {

    private static final String TAG = TokenLoader.class.getSimpleName();

    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private PublishSubject<String> mPublishSubject;
    private Observable<String> mTokenObservable;
    private PublishProcessor<String> mPublishProcessor;
    private Flowable<String> mTokenFlow;

    private TokenLoader() {
        mPublishSubject = PublishSubject.create();
        mTokenObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Thread.sleep(1000);
                Log.d(TAG, "发送Token");
                e.onNext(String.valueOf(System.currentTimeMillis()));
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String token) throws Exception {
                Log.d(TAG, "存储Token=" + token);
                Store.getInstance().setToken(token);
                mRefreshing.set(false);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mRefreshing.set(false);
            }
        }).subscribeOn(Schedulers.io()).serialize();
        mPublishProcessor = PublishProcessor.create();
        mTokenFlow = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                Thread.sleep(1000);
                Log.d(TAG, "发送Token");
                e.onNext(String.valueOf(System.currentTimeMillis()));
            }
        }, BackpressureStrategy.BUFFER).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String token) throws Exception {
                Log.d(TAG, "存储Token=" + token);
                Store.getInstance().setToken(token);
                mRefreshing.set(false);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mRefreshing.set(false);
            }
        }).subscribeOn(Schedulers.io());
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
        if (mRefreshing.compareAndSet(false, true)) {
            Log.d(TAG, "没有请求，发起一次新的Token请求");
            startTokenRequest();
        } else {
            Log.d(TAG, "已经有请求，直接返回等待");
        }
        return mPublishSubject;
    }

    private void startTokenRequest() {
        mTokenObservable.subscribe(mPublishSubject);
    }

    public Flowable<String> getNetTokenFlowLocked() {
        if (mRefreshing.compareAndSet(false, true)) {
            Log.d(TAG, "没有请求，发起一次新的Token请求");
            startTokenFlowRequest();
        } else {
            Log.d(TAG, "已经有请求，直接返回等待");
        }
        return mPublishProcessor;
    }

    private void startTokenFlowRequest() {
        mTokenFlow.subscribe(mPublishProcessor);
    }

}
