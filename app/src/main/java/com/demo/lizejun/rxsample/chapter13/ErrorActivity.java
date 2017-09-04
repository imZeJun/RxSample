package com.demo.lizejun.rxsample.chapter13;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

public class ErrorActivity extends AppCompatActivity {

    private static final String TAG = ErrorActivity.class.getSimpleName();

    private Button mBtnPublish;
    private TextView mTvSubscribe1;
    private TextView mTvSubscribe2;
    private int mIndex;

    private PublishSubject<Integer> mPublishSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        mBtnPublish = (Button) findViewById(R.id.bt_publish);
        mBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishEvent();
            }
        });
        mTvSubscribe1 = (TextView) findViewById(R.id.tv_subscribe_1);
        mTvSubscribe2 = (TextView) findViewById(R.id.tv_subscribe_2);
        mPublishSubject = PublishSubject.create();
        mPublishSubject.map(new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                if (integer == 4) {
                    throw new RuntimeException();
                }
                return integer;
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {

            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {

                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, "发生错误=" + throwable);
                        return Observable.just(0);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(getLambdaObserver());
    }

    private void publishEvent() {
        mPublishSubject.onNext(mIndex++);
    }

    private Observer<Integer> getNormalObserver() {
        return new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext=" + value);
                if (value == 8) {
                    throw new RuntimeException("message");
                } else {
                    Log.d(TAG, "accept=" + value);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }

    private LambdaObserver<Integer> getLambdaObserver() {
        return new LambdaObserver<>(new Consumer<Integer>() {

            @Override
            public void accept(Integer value) throws Exception {
                if (value == 8) {
                    throw new RuntimeException("message");
                } else {
                    Log.d(TAG, "accept=" + value);
                }
            }

        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "accept=" + throwable);
            }

        }, new Action() {

            @Override
            public void run() throws Exception {
                Log.d(TAG, "completed");
            }

        }, new Consumer<Disposable>() {

            @Override
            public void accept(Disposable disposable) throws Exception {

            }

        });
    }

}
