package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops;

import rx.Single;

public interface IConnectionWithMaximumStopsUseCase {

    Single<Integer> execute(ConnectionWithMaximumStopUseCaseInput input);

}
