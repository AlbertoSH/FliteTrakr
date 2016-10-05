package com.github.albertosh.flitetrakr.reactive.services;

import com.github.albertosh.flitetrakr.model.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Single;

/**
 * Basic in memory storage
 */
public class ConnectionService implements IConnectionService {

    private final Set<String> cities;
    private final Map<String, List<Connection>> fromConnections;

    public ConnectionService() {
        fromConnections = Collections.synchronizedMap(new HashMap<>());
        cities = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public Observable<Void> addConnection(Connection connection) {
        return Observable.fromCallable(() -> {
            List<Connection> cityConnections = fromConnections.get(connection.getFrom());
            if (cityConnections == null) {
                cityConnections = new ArrayList<>();
                fromConnections.put(connection.getFrom(), cityConnections);
            }
            cityConnections.add(connection);
            cities.add(connection.getFrom());
            cities.add(connection.getTo());
            return null;
        });
    }

    @Override
    public Single<Connection> recoverConnection(String from, String to) {
        return Single.create(emitter -> {
            List<Connection> cityConnections = fromConnections.get(from);
            if (cityConnections != null) {
                for (Connection c : cityConnections) {
                    if (c.getTo().equals(to)) {
                        emitter.onSuccess(c);
                        return;
                    }
                }
            }
            emitter.onError(ConnectionServiceError.connectionNotFound());
        });
    }

    @Override
    public Observable<String> getCities() {
        return Observable.from(cities);
    }

    @Override
    public Observable<String> getDestiniesFromCity(String city) {
        List<Connection> cityConnections = fromConnections.get(city);
        if (cityConnections == null) {
            return Observable.empty();
        } else {
            return Observable.from(cityConnections)
                    .map(Connection::getTo);
        }
    }
}
