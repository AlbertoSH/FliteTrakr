package com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.ICheapestConnectionUseCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ConnectionsBelowPriceUseCase
        implements IConnectionsBelowPriceUseCase {

    private final ICheapestConnectionUseCase cheapestConnectionUseCase;
    private final IConnectionService connectionService;

    public ConnectionsBelowPriceUseCase(ICheapestConnectionUseCase cheapestConnectionUseCase, IConnectionService connectionService) {
        this.cheapestConnectionUseCase = cheapestConnectionUseCase;
        this.connectionService = connectionService;
    }

    @Override
    public Observable<List<MultipleConnections>> execute(ConnectionsBelowPriceUseCaseInput input) {
        return calculateCheapestConnection(input)
                .toObservable()
                .onErrorResumeNext(error -> {
                    if (error instanceof CheapestConnectionUseCaseError) {
                        CheapestConnectionUseCaseError cheapestConnectionUseCaseError = (CheapestConnectionUseCaseError) error;
                        if (cheapestConnectionUseCaseError.getErrorType()
                                .equals(CheapestConnectionUseCaseError.ErrorType.CONNECTION_NOT_FOUND)) {
                            return Observable.error(ConnectionsBelowPriceUseCaseError.connectionNotFound());
                        }
                    }
                    return Observable.error(error);
                })
                .flatMap(cheapestConnection -> {
                    if (cheapestConnection > input.getPrice()) {
                        // The cheapest connection is more expensive than our output
                        // We're done...
                        return Observable.just(Collections.emptyList());
                    } else {
                        Observable<MultipleConnections> output = performDPSSearch(input);

                        return output
                                .scan(Collections.emptyList(), new Func2<List<MultipleConnections>, MultipleConnections, List<MultipleConnections>>() {
                                    @Override
                                    public List<MultipleConnections> call(List<MultipleConnections> accum, MultipleConnections newValue) {
                                        List<MultipleConnections> result = new ArrayList<>(accum);
                                        result.add(newValue);
                                        Collections.sort(result);
                                        return result;
                                    }
                                })
                                .skip(1); // Ignore first empty list
                    }
                });
    }

    private Single<Integer> calculateCheapestConnection(ConnectionsBelowPriceUseCaseInput input) {
        CheapestConnectionUseCaseInput cheapestConnectionUseCaseInput = new CheapestConnectionUseCaseInput.Builder()
                .from(input.getFrom())
                .to(input.getTo())
                .build();
        return cheapestConnectionUseCase.execute(cheapestConnectionUseCaseInput)
                .map(output -> output.getPrice());
    }

    private Observable<MultipleConnections> performDPSSearch(ConnectionsBelowPriceUseCaseInput input) {

        MultipleConnections.Builder connectionBuilder =
                new MultipleConnections.Builder(input.getFrom());

        Observable<MultipleConnections> firstItem = Observable.empty();
        // Concrete case of starting and ending at the same place
        if (input.getFrom().equals(input.getTo())) {
            firstItem = Observable.just(
                    new MultipleConnections.Builder(input.getFrom()).build());
        }

        return Observable.concat(firstItem, performDPSSearch(input.getFrom(), input.getTo(), input.getPrice(), connectionBuilder));
    }

    private Observable<MultipleConnections> performDPSSearch(String from, String to, Integer price,
                                                             MultipleConnections.Builder connectionBuilder) {

        return connectionService.getDestiniesFromCity(from)
                .flatMap(city ->
                        connectionService.recoverConnection(from, city)
                                .toObservable()
                                .filter(new Func1<Connection, Boolean>() {
                                    @Override
                                    public Boolean call(Connection connection) {
                                        return (connection.getPrice() <= price);
                                    }
                                })
                                .flatMap(connection -> {
                                    MultipleConnections.Builder newBuilder = connectionBuilder.clone();
                                    newBuilder.plusPrice(connection.getPrice());
                                    newBuilder.withCity(city);

                                    if (city.equals(to))
                                        return Observable.just(newBuilder.build())
                                                .concatWith(performDPSSearch(city, to,
                                                        price - connection.getPrice(), newBuilder));
                                    else
                                        return performDPSSearch(city, to,
                                                price - connection.getPrice(), newBuilder);
                                })
                                .subscribeOn(Schedulers.computation())
                );
    }


}
