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
import io.reactivex.internal.observers.LambdaObserver;
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
        //upError();
        //downError();
        //upErrorIgnore();
        downErrorIgnore();
    }

    private void upError() {
        mPublishSubject.map(new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                if (integer == 4) {
                    throw new RuntimeException();
                }
                return integer;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(getNormalObserver());
    }

    private void upErrorIgnore() {
        mPublishSubject.map(new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                if (integer == 4) {
                    throw new RuntimeException();
                }
                return integer;
            }
        }).observeOn(AndroidSchedulers.mainThread()).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                //第一步，通过flatMap对错误进行响应。
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, "判断是否要重订阅，throwable=" + throwable);
                        //第二步：根据错误的类型判断是否需要重订阅。
                        return "retry".equals(throwable.getMessage()) ? Observable.just(0) : Observable.empty();
                    }
                });
            }
        }).subscribe(getNormalObserver());
    }

    private Observer<Integer> getNormalObserver() {
        return new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext=" + value);
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

    private void downError() {
        mPublishSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(getErrorObserver());
    }

    private void downErrorIgnore() {
        mPublishSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(getIgnoreErrorObserver());
    }

    private LambdaObserver<Integer> getErrorObserver() {
        return new LambdaObserver<>(new Consumer<Integer>() {
            @Override
            public void accept(Integer value) throws Exception {
                Log.d(TAG, "onNext=" + value);
                if (value == 4) {
                    Log.d(TAG, "onNext 发生错误");
                    throw new RuntimeException();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "onError=" + throwable);
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "onComplete");
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        });
    }

    private IgnoreErrorLambdaObserver<Integer> getIgnoreErrorObserver() {
        return new IgnoreErrorLambdaObserver<>(new Consumer<Integer>() {
            @Override
            public void accept(Integer value) throws Exception {
                Log.d(TAG, "onNext=" + value);
                if (value == 4) {
                    Log.d(TAG, "onNext 发生错误");
                    throw new RuntimeException();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "onError=" + throwable);
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "onComplete");
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        });
    }

    private void publishEvent() {
        mIndex++;
        Log.d(TAG, "publishEvent=" + mIndex);
        mPublishSubject.onNext(mIndex);
    }



}
