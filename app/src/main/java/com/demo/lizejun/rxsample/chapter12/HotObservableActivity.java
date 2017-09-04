package com.demo.lizejun.rxsample.chapter12;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class HotObservableActivity extends AppCompatActivity {

    private static final String TAG = "RxJavaHotObservable";

    private Button mBtnSubscribe1;
    private Button mBtnSubscribe2;
    private Button mBtnDispose1;
    private Button mBtnDispose2;
    private Button mBtnDispose;

    private Observable<Integer> mColdObservable;
    private Disposable mConvertDisposable;
    private Observable<Integer> mConvertObservable;
    private Disposable mDisposable1;
    private Disposable mDisposable2;
    private List<Integer> mSubscribe1In = new ArrayList<>();
    private List<Integer> mSubscribe2In = new ArrayList<>();
    private List<Integer> mSourceOut = new ArrayList<>();
    private TextView mTvSubscribe1;
    private TextView mTvSubscribe2;
    private TextView mTvSource;
    private Handler mMainHandler;

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_observable);
        mSourceOut = Collections.synchronizedList(new ArrayList<Integer>());
        mSubscribe1In = Collections.synchronizedList(new ArrayList<Integer>());
        mSubscribe2In = Collections.synchronizedList(new ArrayList<Integer>());
        mTvSubscribe1 = (TextView) findViewById(R.id.tv_subscribe_1);
        mTvSubscribe2 = (TextView) findViewById(R.id.tv_subscribe_2);
        mBtnSubscribe1 = (Button) findViewById(R.id.bt_subscribe_1);
        mBtnSubscribe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubscribe1();
            }
        });
        mBtnSubscribe2 = (Button) findViewById(R.id.bt_subscribe_2);
        mBtnSubscribe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubscribe2();
            }
        });
        mBtnDispose1 = (Button) findViewById(R.id.bt_dispose_1);
        mBtnDispose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disposeSubscribe1();
            }
        });
        mBtnDispose2 = (Button) findViewById(R.id.bt_dispose_2);
        mBtnDispose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disposeSubscribe2();
            }
        });
        mBtnDispose = (Button) findViewById(R.id.bt_dispose);
        mBtnDispose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispose();
            }
        });
        mTvSource = (TextView) findViewById(R.id.tv_source);
        mMainHandler = new Handler(Looper.getMainLooper());
        createObservable();
    }

    private void createObservable() {
        //createColdSource(); //(1)订阅到cold;
        //createPublishSource(); //(2)订阅到cold.publish();
        //createShareSource(); //(3)订阅到cold.share();
        //createAutoConnectSource(); //(4)订阅到cold.publish().autoConnect(2);
        createReplySource(); //(5)订阅到cold.reply(3);
    }

    //直接订阅Cold Observable。
    private void createColdSource() {
        mConvertObservable = getSource();
    }

    //.publish()将源Observable转换成为HotObservable，当调用它的connect方法后，无论此时有没有订阅者，源Observable都开始发送数据，订阅者订阅后将可以收到数据，并且订阅者解除订阅不会影响源Observable数据的发射。
    public void createPublishSource() {
        mColdObservable = getSource();
        mConvertObservable = mColdObservable.publish();
        mConvertDisposable = ((ConnectableObservable<Integer>) mConvertObservable).connect();
    }

    //.share()相当于.publish().refCount()，当有订阅者订阅时，源订阅者会开始发送数据，如果所有的订阅者都取消订阅，源Observable就会停止发送数据。
    private void createShareSource() {
        mColdObservable = getSource();
        mConvertObservable = mColdObservable.publish().refCount();
    }

    //.autoConnect在有指定个订阅者时开始让源Observable发送消息，但是订阅者是否取消订阅不会影响到源Observable的发射。
    private void createAutoConnectSource() {
        mColdObservable = getSource();
        mConvertObservable = mColdObservable.publish().autoConnect(1, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                mConvertDisposable = disposable;
            }
        });
    }

    //.reply会让缓存源Observable的N个数据项，当有新的订阅者订阅时，它会发送这N个数据项给它。
    private void createReplySource() {
        mColdObservable = getSource();
        mConvertObservable = mColdObservable.replay(3);
        mConvertDisposable = ((ConnectableObservable<Integer>) mConvertObservable).connect();
    }

    private void startSubscribe1() {
        if (mConvertObservable != null && mDisposable1 == null) {
            mDisposable1 = mConvertObservable.subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    Log.d(TAG, "订阅者1收到数据=" + integer + ",接收线程ID=" + Thread.currentThread().getId());
                    mSubscribe1In.add(integer);
                    updateMessage();
                }
            });
        }
    }

    private void disposeSubscribe1() {
        if (mDisposable1 != null) {
            mDisposable1.dispose();
            mDisposable1 = null;
            mSubscribe1In.clear();
            updateMessage();
        }
    }

    private void startSubscribe2() {
        if (mConvertObservable != null && mDisposable2 == null) {
            mDisposable2 = mConvertObservable.subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    Log.d(TAG, "订阅者2收到数据=" + integer + ",接收线程ID=" + Thread.currentThread().getId());
                    mSubscribe2In.add(integer);
                    updateMessage();
                }
            });
        }
    }

    private void disposeSubscribe2() {
        if (mDisposable2 != null) {
            mDisposable2.dispose();
            mDisposable2 = null;
            mSubscribe2In.clear();
            updateMessage();
        }
    }

    private void dispose() {
        if (mConvertDisposable != null) {
            mConvertDisposable.dispose();
            mConvertDisposable = null;
        }
    }

    private Observable<Integer> getSource() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                try {
                    int i = 0;
                    while (true) {
                        Log.d(TAG, "源被订阅者发射数据=" + i + ",发送线程ID=" + Thread.currentThread().getId());
                        mSourceOut.add(i);
                        observableEmitter.onNext(i++);
                        updateMessage();
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).subscribeOn(Schedulers.io());
    }

    private void updateMessage() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mTvSource.setText(toMessage("源 Observer 发射历史", mSourceOut));
                mTvSubscribe1.setText(toMessage("订阅者1", mSubscribe1In));
                mTvSubscribe2.setText(toMessage("订阅者2", mSubscribe2In));
            }
        });
    }

    private String toMessage(String prefix, List<Integer> set) {
        String result = prefix + ":    ";
        for (Integer data : set) {
            result = result + data + ",";
        }
        return result;
    }


}
