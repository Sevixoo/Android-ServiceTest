package com.sevixoo.servicetest;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sevixoo.servicetest.service.BoundWorkerService;

/**
 * Created by Seweryn on 15.08.2016.
 */
public class TestApplication extends Application {

    public static TestApplication get(Context context){
        return (TestApplication)context.getApplicationContext();
    }

}
