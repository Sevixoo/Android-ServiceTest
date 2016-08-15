package com.sevixoo.servicetest.usecase;

/**
 * Created by Seweryn on 14.08.2016.
 */
public interface UseCaseCallback<ResultType> {
    void onResult( ResultType resultType );
    void onError( Throwable ex );
}
