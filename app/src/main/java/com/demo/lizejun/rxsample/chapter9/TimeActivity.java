package com.demo.lizejun.rxsample.chapter9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lizejun.rxsample.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimeActivity extends AppCompatActivity {

    private static final String TAG = TimeActivity.class.getSimpleName();

    private Button mTvTime1;
    private Button mTvTime2;
    private Button mTvTime3;
    private Button mTvTime4;
    private Button mTvTime5;

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        mTvTime1 = (Button) findViewById(R.id.bt_time_1);
        mTvTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeDemo1();
            }
        });
        mTvTime2 = (Button) findViewById(R.id.bt_time_2);
        mTvTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeDemo2();
            }
        });
        mTvTime3 = (Button) findViewById(R.id.bt_time_3);
        mTvTime3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeDemo3();
            }
        });
        mTvTime4 = (Button) findViewById(R.id.bt_time_4);
        mTvTime4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeDemo4();
            }
        });
        mTvTime5 = (Button) findViewById(R.id.bt_time_5);
        mTvTime5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeDemo5();
            }
        });
        mCompositeDisposable = new CompositeDisposable();
    }

    //延迟 1s 后执行一个任务，然后结束
    private void startTimeDemo1() {
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    //每隔 1s 执行一次任务，第一次任务执行前有 2s 的间隔，执行无限次
    private void startTimeDemo2() {
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(1000, TimeUnit.MILLISECONDS).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    //每隔 1s 执行一次任务，立即执行第一次任务，执行无限次
    private void startTimeDemo3() {
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    //每隔 1s 执行一次任务，立即执行第一次任务，只执行五次
    private void startTimeDemo4() {
        Log.d(TAG, "startTimeDemo4");
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS).take(5).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    //先执行一个任务，等待 1s，再执行另一个任务，然后结束
    private void startTimeDemo5() {
        Log.d(TAG, "startTimeDemo5");
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.just(0L).doOnNext(new Consumer<Long>() {

            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "执行第一个任务");
            }

        }).delay(1000, TimeUnit.MILLISECONDS).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver<Long> getTimeDemoObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long data) {
                Log.d(TAG, "DisposableObserver, onNext=" + data + ",threadId=" + Thread.currentThread().getId());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "DisposableObserver, onError=" + throwable);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "DisposableObserver, onComplete");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
