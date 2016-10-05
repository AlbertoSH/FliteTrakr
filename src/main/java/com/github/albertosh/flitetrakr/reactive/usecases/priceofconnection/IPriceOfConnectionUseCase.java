package com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection;

import rx.Single;

public interface IPriceOfConnectionUseCase {

    Single<Integer> execute(PriceOfConnectionUseCaseInput input);

}
