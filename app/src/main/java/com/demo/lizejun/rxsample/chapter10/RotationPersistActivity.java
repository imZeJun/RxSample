package com.demo.lizejun.rxsample.chapter10;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import org.reactivestreams.Subscription;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.DisposableSubscriber;

public class RotationPersistActivity extends AppCompatActivity implements IHolder {

    private static final String TAG = RotationPersistActivity.class.getName();

    private Button mBtWorker;
    private TextView mTvResult;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_persist);
        mBtWorker = (Button) findViewById(R.id.bt_start_worker);
        mBtWorker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startWorker();
            }

        });
        mTvResult = (TextView) findViewById(R.id.tv_worker_result);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void setWorker(Flowable<Integer> workerFlow) {
        DisposableSubscriber<Integer> disposableObserver = new DisposableSubscriber<Integer>() {

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "disposableObserver onNext");
                mTvResult.setText("integer=" + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "disposableObserver onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "disposableObserver onError");
            }
        };
        workerFlow.doOnSubscribe(new Consumer<Subscription>() {

            @Override
            public void accept(Subscription subscription) throws Exception {
                Log.d(TAG, "disposableObserver doOnSubscribe");
                subscription.request(100);
            }

        }).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private void startWorker() {
        Fragment worker = getWorkerFragment();
        if (worker == null) {
            addWorkerFragment();
        } else {
            Log.d(TAG, "startWorker, workerFragment 已经存在");
        }
    }

    private void addWorkerFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(new WorkerFragment(), WorkerFragment.TAG);
        transaction.commit();
    }

    private Fragment getWorkerFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentByTag(WorkerFragment.TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompositeDisposable.clear();
    }
}
