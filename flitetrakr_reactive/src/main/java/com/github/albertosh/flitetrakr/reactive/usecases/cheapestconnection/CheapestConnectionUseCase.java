package com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.reactive.services.ConnectionServiceError;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

public class CheapestConnectionUseCase
        implements ICheapestConnectionUseCase {

    private final IConnectionService connectionService;

    public CheapestConnectionUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }


    @Override
    public Single<CheapestConnectionUseCaseOutput> execute(CheapestConnectionUseCaseInput input) {
        // Dijkstra's algorithm
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> father = new HashMap<>();
        Map<String, Boolean> seen = new HashMap<>();
        PriorityQueue<Pair<String, Integer>> queue = new PriorityQueue<>((o1, o2) -> o1.second.compareTo(o2.second));

        return connectionService.getCities()
                .doOnNext(city -> {
                    // Init values
                    distance.put(city, Integer.MAX_VALUE);
                    father.put(city, null);
                    seen.put(city, false);
                })
                .toList()
                .flatMap(list -> {
                    if (input.getFrom().equals(input.getTo())) {
                        return connectionService.getDestiniesFromCity(input.getFrom())
                                .flatMap(city -> connectionService.recoverConnection(input.getFrom(), city)
                                        .toObservable())
                                .doOnNext(connection -> {
                                    queue.add(Pair.create(connection.getTo(), connection.getPrice()));
                                    distance.put(connection.getTo(), connection.getPrice());
                                    father.put(connection.getTo(), input.getFrom());
                                })
                                .toList()
                                .map(l -> list);
                    } else {
                        distance.put(input.getFrom(), 0);
                        queue.add(Pair.create(input.getFrom(), 0));
                        return Observable.just(list);
                    }
                })
                .flatMap(list -> round(distance, father, seen, queue))
                .flatMap(aVoid -> {
                    if (father.get(input.getTo()) == null) {
                        return Observable.error(CheapestConnectionUseCaseError.connectionNotFound());
                    } else {
                        List<String> citiesVisited = new ArrayList<>();
                        String cityVisited = input.getTo();
                        citiesVisited.add(cityVisited);
                        do {
                            cityVisited = father.get(cityVisited);
                            citiesVisited.add(0, cityVisited);
                        } while ((cityVisited != null) && (!cityVisited.equals(input.getFrom())));
                        return Observable.just(new CheapestConnectionUseCaseOutput.Builder()
                                .cities(citiesVisited)
                                .price(distance.get(input.getTo()))
                                .build());
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable>() {
                    @Override
                    public Observable call(Throwable error) {
                        if (error instanceof ConnectionServiceError) {
                            ConnectionServiceError connectionServiceError = (ConnectionServiceError) error;
                            if (connectionServiceError.getErrorType().equals(ConnectionServiceError.ErrorType.CONNECTION_NOT_FOUND))
                                return Observable.error(CheapestConnectionUseCaseError.connectionNotFound());
                        }
                        return Observable.error(error);
                    }
                })
                .toSingle();
    }


    private Observable round(Map<String, Integer> distance,
                             Map<String, String> father,
                             Map<String, Boolean> seen,
                             PriorityQueue<Pair<String, Integer>> queue) {
        Pair<String, Integer> minDistance = queue.poll();

        return connectionService.getDestiniesFromCity(minDistance.first)
                .filter(city -> !seen.get(city))
                .flatMap(city ->
                        connectionService.recoverConnection(minDistance.first, city).toObservable())
                .filter(connection -> distance.get(connection.getTo()) >
                        distance.get(minDistance.first) + connection.getPrice())
                .doOnNext(connection -> {
                    String city = connection.getTo();
                    distance.put(city, distance.get(minDistance.first) + connection.getPrice());
                    father.put(city, minDistance.first);
                    queue.add(Pair.create(city, distance.get(city)));
                })
                .toList()
                .flatMap(l -> {
                    if (queue.isEmpty())
                        return Observable.just(null);
                    else
                        return round(distance, father, seen, queue);
                });
    }

}

