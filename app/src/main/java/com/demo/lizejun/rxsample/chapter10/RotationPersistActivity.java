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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;

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
    public void onWorkerPrepared(ConnectableObservable<Long> workerSource) {
        DisposableObserver<Long> disposableObserver = new DisposableObserver<Long>() {

            @Override
            public void onNext(Long aLong) {
                mTvResult.setText("当前进度=" + aLong);
            }

            @Override
            public void onError(Throwable throwable) {
                onWorkerFinished();
            }

            @Override
            public void onComplete() {
                onWorkerFinished();
            }

        };
        workerSource.observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
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
        removeWorkerFragment();
    }

    private void addWorkerFragment() {
        WorkerFragment workerFragment = new WorkerFragment();
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
        mCompositeDisposable.clear();
    }
}
