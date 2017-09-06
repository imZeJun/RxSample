package com.demo.lizejun.rxsample.chapter14;

import android.content.pm.ProviderInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.utils.Store;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TokenActivity extends AppCompatActivity {

    private static final String TAG = TokenActivity.class.getSimpleName();
    private static final String ERROR_TOKEN = "error_token";
    private static final String ERROR_RETRY = "error_retry";

    private Button mBtnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        mBtnRequest = (Button) findViewById(R.id.bt_request);
        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRequest(0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startRequest(1);
            }
        });
    }

    private void startRequest(final int index) {
        Observable<String> observable = Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                String cacheToken = TokenLoader.getInstance().getCacheToken();
                Log.d(TAG, index + "获取到缓存Token=" + cacheToken);
                return Observable.just(cacheToken);
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String token) throws Exception {
                return getUserObservable(index, token);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {

            private int mRetryCount = 0;

            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {

                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, index + ":" + "发生错误=" + throwable + ",重试次数=" + mRetryCount);
                        if (mRetryCount > 0) {
                            return Observable.error(new Throwable(ERROR_RETRY));
                        } else if (ERROR_TOKEN.equals(throwable.getMessage())) {
                            mRetryCount++;
                            return TokenLoader.getInstance().getNetTokenLocked();
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        });
        DisposableObserver<String> observer = new DisposableObserver<String>() {

            @Override
            public void onNext(String value) {
                Log.d(TAG, index + ":" + "收到信息=" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, index + ":" + "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, index + ":" + "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private Observable<String> getUserObservable (final int index, final String token) {
        return Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, index + "使用token=" + token + "发起请求");
                //模拟根据Token去请求信息的过程。
                if (!TextUtils.isEmpty(token) && System.currentTimeMillis() - Long.valueOf(token) < 2000) {
                    e.onNext(index + ":" + token + "的用户信息");
                } else {
                    e.onError(new Throwable(ERROR_TOKEN));
                }
            }
        });
    }
}
