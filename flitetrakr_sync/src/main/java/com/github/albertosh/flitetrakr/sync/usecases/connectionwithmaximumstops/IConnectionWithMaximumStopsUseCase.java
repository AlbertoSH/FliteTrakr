package com.github.albertosh.flitetrakr.sync.usecases.connectionwithmaximumstops;


import com.github.albertosh.flitetrakr.sync.usecases.UseCase;

public interface IConnectionWithMaximumStopsUseCase
        extends UseCase<ConnectionWithMaximumStopUseCaseInput,
        Integer,
        RuntimeException> {
}
