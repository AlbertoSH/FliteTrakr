package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.sync.usecases.UseCase;

import java.util.List;

public interface IConnectionsBelowPriceUseCase
        extends UseCase<ConnectionsBelowPriceUseCaseInput,
        List<MultipleConnections>, ConnectionsBelowPriceUseCaseError> {
}
