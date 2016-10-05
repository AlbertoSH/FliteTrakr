package com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionServiceError;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.util.Pair;

import java.util.List;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class PriceOfConnectionUseCase
        implements IPriceOfConnectionUseCase {

    private final IConnectionService connectionService;

    public PriceOfConnectionUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }

    @Override
    public Single<Integer> execute(PriceOfConnectionUseCaseInput input) {
        final List<String> codes = input.getCodes();

        return Observable.create((Observable.OnSubscribe<Pair<String, String>>) subscriber -> {
            int connectionSize = codes.size();
            for (int i = 1; i < connectionSize; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                Pair<String, String> cityPair = Pair.create(codes.get(i - 1), codes.get(i));
                subscriber.onNext(cityPair);
            }
            subscriber.onCompleted();
        })
                .flatMap(pair -> connectionService.recoverConnection(pair.first, pair.second)
                        .subscribeOn(Schedulers.io())
                        .toObservable())
                .map(Connection::getPrice)
                .reduce(0, (accum, price) -> accum + price)
                .onErrorResumeNext(error -> {
                    if (error instanceof ConnectionServiceError) {
                        ConnectionServiceError connectionServiceError = (ConnectionServiceError) error;
                        if (connectionServiceError.getErrorType().equals(ConnectionServiceError.ErrorType.CONNECTION_NOT_FOUND))
                            return Observable.error(PriceOfConnectionUseCaseError.connectionNotFound());
                    }
                    return Observable.error(error);
                }).toSingle();
    }


}
