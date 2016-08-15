package com.sevixoo.servicetest.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sevixoo.servicetest.R;
import com.sevixoo.servicetest.service.BoundWorkerService;

public class SecondActivity extends AppCompatActivity {

    BoundWorkerService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = new Intent(getApplicationContext(), BoundWorkerService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, mServiceConnection , BIND_AUTO_CREATE);

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("SecondActivity","onServiceConnected");
            mService = ((BoundWorkerService.BoundWorkerBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Log.e("SecondActivity","onServiceDisconnected");
            finish();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mService!=null){
            getApplicationContext().unbindService(mServiceConnection);
        }
    }


}
