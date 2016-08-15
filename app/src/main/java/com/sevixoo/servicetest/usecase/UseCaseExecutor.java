package com.sevixoo.servicetest.usecase;

/**
 * Created by Seweryn on 14.08.2016.
 */
public interface UseCaseExecutor {
    String execute( UseCase useCase , UseCaseCallback useCaseCallback , ExecutionStrategy executionStrategy );
}
