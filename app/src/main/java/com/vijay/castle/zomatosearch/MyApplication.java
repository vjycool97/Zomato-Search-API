package com.vijay.castle.zomatosearch;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by vijay on 6/9/17.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static Context mContext;
    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
    }

    public static Gson getGsonInstance() {
        if (gson != null) {
            return gson;
        } else {
            gson = new Gson();
            return gson;
        }
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    public static synchronized MyApplication getContext() {
        return mInstance;
    }
}

