package com.demo.lizejun.rxsample.chapter11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.demo.lizejun.rxsample.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class DetectorActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;
    private TextView mTvNetworkResult;
    private PublishSubject<Boolean> mPublishSubject;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector);
        mTvNetworkResult = (TextView) findViewById(R.id.tv_network_result);
        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean aBoolean) {
                String message = "网络状态变化=" + (aBoolean ? "开启" : "关闭");
                Toast.makeText(DetectorActivity.this, message, Toast.LENGTH_SHORT).show();
                mTvNetworkResult.setText(message);
            }

            @Override
            public void onError(Throwable throwable) {
                mTvNetworkResult.setText("发生错误");
            }

            @Override
            public void onComplete() {
                mTvNetworkResult.setText("完成");
            }
        };
        mPublishSubject = PublishSubject.create();
        mPublishSubject.distinctUntilChanged().skip(1).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(observer);
        registerBroadcast();
    }

    private void registerBroadcast() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPublishSubject != null) {
                    mPublishSubject.onNext(isNetworkConnected());
                }
            }

        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    public void unRegisterBroadcast() {
        unregisterReceiver(mReceiver);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
        mCompositeDisposable.clear();
    }
}
