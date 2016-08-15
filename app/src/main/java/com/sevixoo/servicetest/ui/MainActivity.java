package com.sevixoo.servicetest.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sevixoo.servicetest.R;
import com.sevixoo.servicetest.service.BoundWorkerService;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SAVED_STATE = "saved_state";

    private static class InstanceState implements Serializable{
        public String presenterID;
        public String someUseCaseReqID;
    }

    private MainPresenter       mPresenter;
    private BoundWorkerService  mService;

    private Button              mBtnDoSomeWork;
    private ProgressDialog      progressDialog;

    private TextView            mTextViewLog;
    private InstanceState       mState;

    private Button              mBtn;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnDoSomeWork = (Button)findViewById(R.id.button_do_some_work);
        mTextViewLog = (TextView)findViewById(R.id.textView_list);


        mBtn = (Button)findViewById(R.id.button_second_activity);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getBaseContext() , SecondActivity.class );
                startActivity(intent);
            }
        });

        if(savedInstanceState==null) {
            mState = new InstanceState();
            mPresenter = new MainPresenter();
            mPresenter.setView(MainActivity.this);
        }else{
            mState = (InstanceState)savedInstanceState.getSerializable(SAVED_STATE);
        }

        mBtnDoSomeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mState.someUseCaseReqID = mPresenter.doSomeUseCase( "param" );
            }
        });

        Intent intent = new Intent(getApplicationContext(), BoundWorkerService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, mServiceConnection , BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((BoundWorkerService.BoundWorkerBinder)iBinder).getService();
            if(mPresenter==null){
                mPresenter = (MainPresenter) mService.getPresenter( mState.presenterID );
                mPresenter.setView(MainActivity.this);
                onStateRestored();
            }else{
                mState.presenterID = mService.registerPresenter(mPresenter);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            finish();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_STATE,mState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
        mPresenter.setView(null);
        if(mService!=null){
            getApplicationContext().unbindService(mServiceConnection);
        }
    }

    public void displayList(List<String> list){
        String output = "";
        for (String s:list) {
            output += s + "\n";
        }
        mTextViewLog.setText(output);
    }

    public void displayError( String error ){
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    public void showProgress(){
        if(progressDialog==null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
        }
    }

    public void hideProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void onStateRestored(){
        if(mState.someUseCaseReqID!=null) {
            if (mService.exist(mState.someUseCaseReqID)) {
                if(mService.isPending(mState.someUseCaseReqID)){
                    showProgress();
                }else{
                    hideProgress();
                    mService.redeliver(mState.someUseCaseReqID);
                    mState.someUseCaseReqID = null;
                }
            } else {
                mState.someUseCaseReqID = null;
            }
        }
    }

}
