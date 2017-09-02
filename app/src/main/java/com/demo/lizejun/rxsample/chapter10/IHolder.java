package com.demo.lizejun.rxsample.chapter10;


import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface IHolder {
    public void onWorkerPrepared(Observable<String> worker);
}

