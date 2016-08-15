package com.sevixoo.servicetest.usecase;

/**
 * Created by Seweryn on 14.08.2016.
 */
public interface UseCase<ResultType> {
    ResultType execute()throws Exception;
}
