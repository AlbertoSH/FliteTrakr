package com.github.albertosh.flitetrakr.sync.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop.ConnectionWithExactStopUseCaseInput;
import com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop.IConnectionWithExactStopUseCase;

public class ConnectionWithMaximumStopUseCase
        implements IConnectionWithMaximumStopsUseCase {

    private final IConnectionWithExactStopUseCase connectionWithExactStopUseCase;

    public ConnectionWithMaximumStopUseCase(IConnectionWithExactStopUseCase connectionWithExactStopUseCase) {
        this.connectionWithExactStopUseCase = connectionWithExactStopUseCase;
    }

    @Override
    public Integer execute(ConnectionWithMaximumStopUseCaseInput input) {
        int maxStops = input.getStops();
        int result = 0;
        for (int i = 0; i <= maxStops; i++) {
            result += executeUseCaseWithStops(input, i);
        }
        return result;
    }

    private int executeUseCaseWithStops(ConnectionWithMaximumStopUseCaseInput input, int stops) {
        ConnectionWithExactStopUseCaseInput exactInput = new ConnectionWithExactStopUseCaseInput.Builder()
                .from(input.getFrom())
                .to(input.getTo())
                .stops(stops)
                .build();
        return connectionWithExactStopUseCase.execute(exactInput);
    }

}
