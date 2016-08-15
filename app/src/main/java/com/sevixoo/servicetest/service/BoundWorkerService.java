package com.sevixoo.servicetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sevixoo.servicetest.threading.AsyncExecutor;
import com.sevixoo.servicetest.threading.BackgroundThread;
import com.sevixoo.servicetest.threading.MainHandler;
import com.sevixoo.servicetest.usecase.ExecutionStrategy;
import com.sevixoo.servicetest.usecase.Presenter;
import com.sevixoo.servicetest.usecase.UseCase;
import com.sevixoo.servicetest.usecase.UseCaseCallback;
import com.sevixoo.servicetest.usecase.UseCaseExecutor;

import java.util.HashMap;
import java.util.UUID;

public class BoundWorkerService extends Service implements UseCaseExecutor{

    public class BoundWorkerBinder extends Binder{
        public BoundWorkerService getService(){
            return BoundWorkerService.this;
        }
    }

    private static final int STATE_EXECUTING = 0;
    private static final int STATE_DONE = 1;

    private class Task implements Runnable{

        private Object              result;
        private Throwable           exception;
        private int                 state;
        private UseCase             useCase;
        private UseCaseCallback     useCaseCallback;
        private ExecutionStrategy   executionStrategy;
        private String              ID;

        public Task(String ID ,UseCase useCase,@NonNull UseCaseCallback useCaseCallback, ExecutionStrategy executionStrategy) {
            this.ID = ID;
            this.useCase = useCase;
            this.useCaseCallback = useCaseCallback;
            this.executionStrategy = executionStrategy;
        }

        @Override
        public void run() {

            state = STATE_EXECUTING;
            try {
                result = useCase.execute();
            }catch (Exception ex){
                exception = ex;
            }finally {
                state = STATE_DONE;
            }
            if( executionStrategy.canExecuteCallback() ){
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback();
                    }
                });
            }
        }

        private void callback(){
            if(result!=null){
                useCaseCallback.onResult(result);
            }else{
                useCaseCallback.onError(exception);
            }
            removeTask(ID);
        }

        private synchronized boolean isPending(){
            return state == STATE_EXECUTING;
        }

    }


    private HashMap<String,Presenter>                   mRegisteredPresenter;

    private IBinder                                     mBinder;
    private MainHandler                                 mMainHandler;
    private BackgroundThread                            mBackgroundThread;

    private HashMap<String,Task>                        mTaskList;

    public BoundWorkerService() {
        Log.e("BoundWorkerService","onCreate");
        mRegisteredPresenter = new HashMap<>();
        mTaskList = new HashMap<>();
        mBinder = new BoundWorkerBinder();
        mMainHandler = MainHandler.getInstance();
        mBackgroundThread = new AsyncExecutor();
    }

    public Presenter getPresenter(String ID){
        return mRegisteredPresenter.get(ID);
    }

    public String registerPresenter( Presenter presenter ){
        String ID = UUID.randomUUID().toString();
        mRegisteredPresenter.put( ID , presenter );
        presenter.onServiceConnected( this );
        return ID;
    }

    @Override
    public String execute( UseCase useCase, UseCaseCallback useCaseCallback , ExecutionStrategy executionStrategy ) {
        String ID = UUID.randomUUID().toString();
        Task task = new Task(ID,useCase,useCaseCallback,executionStrategy);
        mTaskList.put( ID , task );
        mBackgroundThread.execute(task);
        return ID;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("BoundWorkerService","onStartCommand");
        return START_NOT_STICKY;
    }

    private void removeTask( String taskID ){
        mTaskList.remove(taskID);
    }

    public boolean isPending( String taskID ){
        Task task = mTaskList.get(taskID);
        if(task!=null){
            return task.isPending();
        }
        return false;
    }

    public void redeliver( String taskID ){
        Task task = mTaskList.get(taskID);
        task.callback();
    }

    public boolean exist( String taskID ){
        return mTaskList.get(taskID)!=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BoundWorkerService","onDestroy");
    }
}
