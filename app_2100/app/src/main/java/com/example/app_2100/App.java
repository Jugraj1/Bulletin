package com.example.app_2100;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getContext() {
        return mContext;
    }
}
