package com.demo.lizejun.rxsample.chapter10;


import io.reactivex.Flowable;

public interface IHolder {
    public void setWorker(Flowable<Integer> workerFlow);
}
