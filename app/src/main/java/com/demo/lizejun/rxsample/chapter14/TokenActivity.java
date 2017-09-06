package com.demo.lizejun.rxsample.chapter14;

import android.content.pm.ProviderInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.utils.Store;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TokenActivity extends AppCompatActivity {

    private static final String TAG = TokenActivity.class.getSimpleName();
    private static final String ERROR_TOKEN = "error_token";

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
        getUserObservable(index).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, index + ":" + "发生错误=" + throwable);
                        return TokenLoader.getInstance().getNetTokenLocked();
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String value) throws Exception {
                Log.d(TAG, index + ":" + "获得最终信息=" + value);
            }
        });
    }

    private Observable<String> getUserObservable (final int index) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String cacheToken = TokenLoader.getInstance().getCacheToken();
                Log.d(TAG, index + "获取到缓存Token=" + cacheToken);
                //模拟根据Token去请求信息的过程。
                long tokeTime = Long.valueOf(cacheToken);
                if (System.currentTimeMillis() - tokeTime < 2000) {
                    e.onNext(index + ":" + cacheToken + "的用户信息");
                } else {
                    e.onError(new Throwable(ERROR_TOKEN));
                }
            }
        });
    }
}
