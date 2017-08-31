package com.demo.lizejun.rxsample.chapter10;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;

public class WorkerFragment extends Fragment {

    public static final String TAG = WorkerFragment.class.getName();

    private PublishProcessor<Integer> mWorkerSubject;
    private PublishProcessor<Boolean> mLifeSubject;
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
        mWorkerSubject = PublishProcessor.create();
        mLifeSubject = PublishProcessor.create();
        setRetainInstance(true);
        mWorkerSubject.takeUntil(mLifeSubject);
        Flowable.interval(1, TimeUnit.SECONDS).map(new Function<Long, Integer>() {

            @Override
            public Integer apply(Long aLong) throws Exception {
                Log.d("RotationPersistActivity", "apply=" + aLong);
                return aLong.intValue();
            }

        }).take(20).subscribe(mWorkerSubject);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHolder != null) {
            mHolder.setWorker(mWorkerSubject);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifeSubject.onComplete();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHolder = null;
    }
}
