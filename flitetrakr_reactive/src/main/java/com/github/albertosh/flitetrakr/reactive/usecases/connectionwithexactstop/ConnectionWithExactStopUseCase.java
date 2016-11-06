package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class ConnectionWithExactStopUseCase
        implements IConnectionWithExactStopUseCase {

    private final IConnectionService connectionService;

    public ConnectionWithExactStopUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }

    @Override
    public Single<Integer> execute(ConnectionWithExactStopUseCaseInput input) {
        if (input.getStops() == 0) {
            return connectionService.getDestiniesFromCity(input.getFrom())
                    .filter(city -> city.equals(input.getTo()))
                    .count()
                    .toSingle();
        } else {
            return applyRecursion(input)
                    .toSingle();
        }

    }

    private Observable<Integer> applyRecursion(ConnectionWithExactStopUseCaseInput input) {
        return connectionService.getDestiniesFromCity(input.getFrom())
                .map(city -> new ConnectionWithExactStopUseCaseInput.Builder()
                        .from(city)
                        .to(input.getTo())
                        .stops(input.getStops() - 1)
                        .build())
                .flatMap(newInput -> execute(newInput)
                        .toObservable()
                        .subscribeOn(Schedulers.newThread()))
                .reduce(0, (accum, value) -> accum + value);
    }
}
