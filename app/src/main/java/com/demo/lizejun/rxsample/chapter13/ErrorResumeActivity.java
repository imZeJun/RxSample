package com.demo.lizejun.rxsample.chapter13;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lizejun.rxsample.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class ErrorResumeActivity extends AppCompatActivity {

    private static final String TAG = ErrorResumeActivity.class.getSimpleName();

    private Button mBtnPublish;
    private TextView mTvSubscribe1;
    private TextView mTvSubscribe2;

    private PublishSubject<String> mPublishSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_resume);
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
        DisposableObserver<String> normal = new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(TAG, "observer1, onNext=" + s);
                mTvSubscribe1.setText(s);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "observer1, onError=" + throwable);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "observer1, onComplete");
            }
        };
        DisposableObserver<String> errorCache = new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(TAG, "observer2, onNext=" + s);
                mTvSubscribe2.setText(s);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "observer2, onError=" + throwable);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "observer2, onComplete");
            }
        };
        mPublishSubject.flatMap(new Function<String, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(String s) throws Exception {
                return getErrorObservable();
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribe(normal);
        mPublishSubject.flatMap(new Function<String, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(String s) throws Exception {
                return getErrorObservable().onErrorResumeNext(Observable.<String>never());
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribe(errorCache);
    }

    private Observable<String> getErrorObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                boolean error = Math.random() > 0.5;
                Log.d(TAG, "error=" + error);
                if (error) {
                    observableEmitter.onError(new RuntimeException());
                } else {
                    observableEmitter.onNext(String.valueOf("onNext"));
                }
            }
        });
    }

    private void publishEvent() {
        mPublishSubject.onNext("currentTime=" + System.currentTimeMillis());
    }

}
