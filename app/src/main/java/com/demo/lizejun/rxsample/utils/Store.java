package com.demo.lizejun.rxsample.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class Store {

    private static final String SP_RX = "sp_rx";
    private static final String TOKEN = "token";

    private SharedPreferences mStore;

    private Store() {
        mStore = Utils.getAppContext().getSharedPreferences(SP_RX, Context.MODE_PRIVATE);
    }

    public static Store getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final Store INSTANCE = new Store();
    }

    public void setToken(String token) {
        mStore.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return mStore.getString(TOKEN, "");
    }
}
