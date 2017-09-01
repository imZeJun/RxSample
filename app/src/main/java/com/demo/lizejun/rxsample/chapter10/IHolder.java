package com.demo.lizejun.rxsample.chapter10;


import io.reactivex.observables.ConnectableObservable;

public interface IHolder {
    public void onWorkerPrepared(ConnectableObservable<Long> workerFlow);
}

