package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.IConnectionWithExactStopUseCase;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class ConnectionWithMaximumStopUseCase
        implements IConnectionWithMaximumStopsUseCase {

    private final IConnectionWithExactStopUseCase connectionWithExactStopUseCase;

    public ConnectionWithMaximumStopUseCase(IConnectionWithExactStopUseCase connectionWithExactStopUseCase) {
        this.connectionWithExactStopUseCase = connectionWithExactStopUseCase;
    }

    @Override
    public Single<Integer> execute(ConnectionWithMaximumStopUseCaseInput input) {
        int maxStops = input.getStops();
        return Observable.range(0, maxStops + 1)
                .flatMap(currentStops -> executeUseCaseWithStops(input, currentStops)
                        .toObservable())
                .reduce(0, (accum, value) -> accum + value)
                .toSingle()
                ;

    }

    private Single<Integer> executeUseCaseWithStops(ConnectionWithMaximumStopUseCaseInput input, int stops) {
        ConnectionWithExactStopUseCaseInput exactInput = new ConnectionWithExactStopUseCaseInput.Builder()
                .from(input.getFrom())
                .to(input.getTo())
                .stops(stops)
                .build();
        return connectionWithExactStopUseCase.execute(exactInput);
    }

}
