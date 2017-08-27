package com.demo.lizejun.rxsample.chapter1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DownloadActivity extends AppCompatActivity {

    private TextView mTvDownload;
    private TextView mTvDownloadResult;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        mTvDownload = (TextView) findViewById(R.id.tv_download);
        mTvDownloadResult = (TextView) findViewById(R.id.tv_download_result);
        mTvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }

    private void startDownload() {
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i % 20 == 0) {
                        try {
                            Thread.sleep(500); //模拟下载的操作。
                        } catch (InterruptedException exception) {
                            if (!e.isDisposed()) {
                                e.onError(exception);
                            }
                        }
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }

        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                Log.d("DownloadActivity", "onNext=" + value);
                mTvDownloadResult.setText("当前下载进度：" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("DownloadActivity", "onError=" + e);
                mTvDownloadResult.setText("下载失败");
            }

            @Override
            public void onComplete() {
                Log.d("DownloadActivity", "onComplete");
                mTvDownloadResult.setText("下载成功");
            }
        };
        observable.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
