package com.sevixoo.servicetest.threading;

import android.os.Handler;
import android.os.Looper;

public class MainHandler{

    private static MainHandler sMainThread;

    private android.os.Handler mHandler;

    private MainHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static MainHandler getInstance() {
        if (sMainThread == null) {
            sMainThread = new MainHandler();
        }
        return sMainThread;
    }
}
