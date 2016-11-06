package com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.sync.services.IConnectionService;

import java.util.Set;

public class ConnectionWithExactStopUseCase
        implements IConnectionWithExactStopUseCase {

    private final IConnectionService connectionService;

    public ConnectionWithExactStopUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }

    @Override
    public Integer execute(ConnectionWithExactStopUseCaseInput input) {
        if (input.getStops() == 0) {
            Set<String> connectedCities = connectionService.getDestiniesFromCity(input.getFrom());
            return connectedCities.contains(input.getTo())
                    ? 1
                    : 0;
        } else {
            return applyRecursion(input);
        }

    }

    private Integer applyRecursion(ConnectionWithExactStopUseCaseInput input) {
        Set<String> connectedCities = connectionService.getDestiniesFromCity(input.getFrom());
        return connectedCities.stream()
                .map(city -> new ConnectionWithExactStopUseCaseInput.Builder()
                        .from(city)
                        .to(input.getTo())
                        .stops(input.getStops() - 1)
                        .build())
                .map(this::execute)
                .filter(result -> result > 0)
                .reduce(0, (accum, value) -> accum + value);
    }
}
