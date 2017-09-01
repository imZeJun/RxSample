package com.demo.lizejun.rxsample.chapter10;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class WorkerFragment extends Fragment {

    public static final String TAG = WorkerFragment.class.getName();

    private ConnectableObservable<Long> mConnectObservable; //用于Worker和Holder的连接。
    private Disposable mConnectDisposable;
    private IHolder mHolder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHolder) {
            mHolder = (IHolder) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mConnectObservable != null) {
            return;
        }
        Observable<Long> sourceObservable = Observable.create(new ObservableOnSubscribe<Long>() {

            @Override
            public void subscribe(ObservableEmitter<Long> e) throws Exception {
                for (long i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException error) {
                        if (!e.isDisposed()) {
                            e.onError(error);
                        }
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }

        }).subscribeOn(Schedulers.io());
        mConnectObservable = sourceObservable.publish();
        mConnectDisposable = mConnectObservable.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHolder != null) {
            mHolder.onWorkerPrepared(mConnectObservable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectDisposable.dispose();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHolder = null;
    }
}
