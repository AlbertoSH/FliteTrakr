package com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.MultipleConnections;

import java.util.List;

import rx.Observable;

public interface IConnectionsBelowPriceUseCase {

    Observable<List<MultipleConnections>> execute(ConnectionsBelowPriceUseCaseInput input);

}
