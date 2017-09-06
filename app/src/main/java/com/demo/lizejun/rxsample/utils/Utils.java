package com.demo.lizejun.rxsample.utils;

import android.content.Context;
import android.content.Intent;

public class Utils {

    private static Context sAppContext;

    public static void startActivity(Context context, Class targetName) {
        Intent intent = new Intent(context, targetName);
        context.startActivity(intent);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static void setAppContext(Context appContext) {
        sAppContext = appContext;
    }
}
