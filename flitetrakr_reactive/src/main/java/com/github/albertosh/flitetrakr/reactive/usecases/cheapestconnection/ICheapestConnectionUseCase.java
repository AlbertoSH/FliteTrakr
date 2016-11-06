package com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection;

import rx.Single;

public interface ICheapestConnectionUseCase {

    Single<CheapestConnectionUseCaseOutput> execute(CheapestConnectionUseCaseInput input);

}
