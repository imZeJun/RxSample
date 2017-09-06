package com.demo.lizejun.rxsample;


import android.app.Application;
import com.demo.lizejun.rxsample.utils.Utils;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.setAppContext(this);
    }
}
