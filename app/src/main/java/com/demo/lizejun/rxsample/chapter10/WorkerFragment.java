package com.demo.lizejun.rxsample.chapter10;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class WorkerFragment extends Fragment {

    public static final String TAG = WorkerFragment.class.getName();

    private ConnectableObservable<String> mWorker;
    private Disposable mDisposable;
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
        if (mWorker != null) {
            return;
        }
        Bundle bundle = getArguments();
        final String taskName = (bundle != null ? bundle.getString("task_name") : null);
        mWorker = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {

                for (int i = 0; i < 10; i++) {
                    String message = "任务名称=" + taskName + ", 任务进度=" + i * 10 + "%";
                    try {
                        Log.d(TAG, message);
                        Thread.sleep(1000);
                        //如果已经抛弃，那么不再继续任务。
                        if (observableEmitter.isDisposed()) {
                            break;
                        }
                    } catch (InterruptedException error) {
                        if (!observableEmitter.isDisposed()) {
                            observableEmitter.onError(error);
                        }
                    }
                    observableEmitter.onNext(message);
                }
                observableEmitter.onComplete();
            }

        }).subscribeOn(Schedulers.io()).publish();
        mDisposable = mWorker.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHolder != null) {
            mHolder.onWorkerPrepared(mWorker);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHolder = null;
    }
}
