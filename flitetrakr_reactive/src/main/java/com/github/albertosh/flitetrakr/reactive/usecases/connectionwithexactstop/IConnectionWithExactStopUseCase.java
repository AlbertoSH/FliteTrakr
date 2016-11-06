package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop;

import rx.Single;

public interface IConnectionWithExactStopUseCase {

    Single<Integer> execute(ConnectionWithExactStopUseCaseInput input);

}
