package com.demo.lizejun.rxsample.chapter10;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

public class RotationPersistActivity extends AppCompatActivity implements IHolder {

    private static final String TAG = RotationPersistActivity.class.getName();

    private Button mBtWorker;
    private TextView mTvResult;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
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
    public void onWorkerPrepared(Observable<String> worker) {
        DisposableObserver<String> disposableObserver = new DisposableObserver<String>() {

            @Override
            public void onNext(String message) {
                mTvResult.setText(message);
            }

            @Override
            public void onError(Throwable throwable) {
                onWorkerFinished();
                mTvResult.setText("任务错误");
            }

            @Override
            public void onComplete() {
                onWorkerFinished();
                mTvResult.setText("任务完成");
            }

        };
        worker.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private void startWorker() {
        WorkerFragment worker = getWorkerFragment();
        if (worker == null) {
            addWorkerFragment();
        } else {
            Log.d(TAG, "WorkerFragment has attach");
        }
    }

    private void onWorkerFinished() {
        Log.d(TAG, "onWorkerFinished");
        removeWorkerFragment();
    }

    private void addWorkerFragment() {
        WorkerFragment workerFragment = new WorkerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("task_name", "学习RxJava2");
        workerFragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(workerFragment, WorkerFragment.TAG);
        transaction.commit();
    }

    private void removeWorkerFragment() {
        WorkerFragment workerFragment = getWorkerFragment();
        if (workerFragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(workerFragment);
            transaction.commit();
        }
    }

    private WorkerFragment getWorkerFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return (WorkerFragment) manager.findFragmentByTag(WorkerFragment.TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mCompositeDisposable.clear();
    }
}
