package com.sevixoo.servicetest.ui;

import com.sevixoo.servicetest.ui.MainActivity;
import com.sevixoo.servicetest.usecase.DoSomethingUseCase;
import com.sevixoo.servicetest.usecase.ExecutionStrategy;
import com.sevixoo.servicetest.usecase.Presenter;
import com.sevixoo.servicetest.usecase.UseCaseCallback;
import com.sevixoo.servicetest.usecase.UseCaseExecutor;

import java.util.List;

/**
 * Created by Seweryn on 14.08.2016.
 */
public class MainPresenter implements Presenter {

    MainActivity mView;
    UseCaseExecutor                         mExecutorService;

    public MainPresenter() {

    }

    public void setView(MainActivity activity){
        mView = activity;
    }

    public String doSomeUseCase( String param ){
        mView.showProgress();
        DoSomethingUseCase doSomethingUseCase = new DoSomethingUseCase( param );
        return mExecutorService.execute( doSomethingUseCase , mCallback , executionStrategy );
    }

    private ExecutionStrategy executionStrategy = new ExecutionStrategy() {
        @Override
        public boolean canExecuteCallback() {
            return mView != null;
        }
    };

    private UseCaseCallback<List<String>> mCallback = new UseCaseCallback<List<String>>() {
        @Override
        public void onResult(List<String> list) {
            mView.displayList(list);
            mView.hideProgress();
        }

        @Override
        public void onError(Throwable ex) {
            mView.displayError(ex.getMessage());
            mView.hideProgress();
        }
    };

    @Override
    public void onServiceConnected(UseCaseExecutor executor) {
        mExecutorService = executor;
    }
}
