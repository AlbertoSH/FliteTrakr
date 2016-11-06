package com.github.albertosh.flitetrakr.sync.usecases.priceofconnection;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;

import java.util.List;
import java.util.Optional;

public class PriceOfConnectionUseCase
        implements IPriceOfConnectionUseCase {

    private final IConnectionService connectionService;

    public PriceOfConnectionUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }

    @Override
    public Integer execute(PriceOfConnectionUseCaseInput input) throws PriceOfConnectionUseCaseError {
        Integer accum = 0;
        List<String> codes = input.getCodes();
        int connectionSize = codes.size();
        for (int i = 1; i < connectionSize; i++) {
            Optional<Connection> optConnection = connectionService.recoverConnection(
                    codes.get(i - 1), codes.get(i)
            );
            if (optConnection.isPresent()) {
                accum += optConnection.get().getPrice();
            } else {
                throw PriceOfConnectionUseCaseError.connectionNotFound();
            }
        }
        return accum;
    }
}
