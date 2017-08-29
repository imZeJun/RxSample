package com.demo.lizejun.rxsample.chapter7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.demo.lizejun.rxsample.R;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class CombineLatestActivity extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtPassword;
    private Button mBtLogin;
    private PublishSubject<String> mNameSubject;
    private PublishSubject<String> mPasswordSubject;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_latest);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mNameSubject = PublishSubject.create();
        mPasswordSubject = PublishSubject.create();
        Observable observable = Observable.combineLatest(mNameSubject, mPasswordSubject, new BiFunction<String, String, Boolean>() {

            @Override
            public Boolean apply(String name, String password) throws Exception {
                int nameLen = name.length();
                int passwordLen = password.length();
                return nameLen > 2 && nameLen < 15 && passwordLen > 8 && passwordLen < 16;
            }

        });
        DisposableObserver<Boolean> disposable = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean value) {
                mBtLogin.setEnabled(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        };
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposable);
    }
}
