package com.demo.lizejun.rxsample.chapter5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lizejun.rxsample.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PollingActivity extends AppCompatActivity {

    private TextView mTvSimple;
    private TextView mTvAdvance;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);
        mTvSimple = (TextView) findViewById(R.id.tv_simple);
        mTvSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSimplePolling();
            }
        });
        mTvAdvance = (TextView) findViewById(R.id.tv_advance);
        mTvAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvancePolling();
            }
        });
        mCompositeDisposable = new CompositeDisposable();
    }

    private void startSimplePolling() {
        Observable<Long> observable = Observable.interval(3000, TimeUnit.MILLISECONDS).take(5);
        DisposableObserver<Long> disposableObserver = getDisposableObserver();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private void startAdvancePolling() {
        Observable<Long> observable = Observable.just(1L).repeatWhen(new Function<Observable<Object>, ObservableSource<Long>>() {

            private long mRepeatCount;

            @Override
            public ObservableSource<Long> apply(Observable<Object> objectObservable) throws Exception {
                //必须作出反应，这里是通过flatMap操作符。
                return objectObservable.flatMap(new Function<Object, ObservableSource<Long>>() {

                    @Override
                    public ObservableSource<Long> apply(Object o) throws Exception {
                        if (mRepeatCount++ >= 5) {
                            return Observable.empty();
                        }
                        return Observable.timer(3000 + mRepeatCount * 1000, TimeUnit.MILLISECONDS);
                    }

                });
            }
        });
        DisposableObserver<Long> disposableObserver = getDisposableObserver();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver<Long> getDisposableObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long aLong) {
                doWork();
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {
                Toast.makeText(PollingActivity.this, "所有任务完成", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void doWork() {
        long workTime = (long) (Math.random() * 500) + 500;
        try {
            Thread.sleep(workTime);
            Toast.makeText(PollingActivity.this, "单次任务完成，耗时：" + workTime, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
