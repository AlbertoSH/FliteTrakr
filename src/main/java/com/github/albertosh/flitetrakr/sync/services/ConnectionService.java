package com.github.albertosh.flitetrakr.sync.services;

import com.github.albertosh.flitetrakr.model.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Basic in memory storage
 */
public class ConnectionService implements IConnectionService {

    private final Set<String> cities;
    private final Map<String, List<Connection>> fromConnections;

    public ConnectionService() {
        fromConnections = new HashMap<>();
        cities = new HashSet<>();
    }

    @Override
    public void addConnection(Connection connection) {
        List<Connection> cityConnections = fromConnections.get(connection.getFrom());
        if (cityConnections == null) {
            cityConnections = new ArrayList<>();
            fromConnections.put(connection.getFrom(), cityConnections);
        }
        cityConnections.add(connection);
        cities.add(connection.getFrom());
        cities.add(connection.getTo());
    }

    @Override
    public Optional<Connection> recoverConnection(String from, String to) {
        List<Connection> cityConnections = fromConnections.get(from);
        if (cityConnections == null) {
            return Optional.empty();
        } else {
            for (Connection c : cityConnections) {
                if (c.getTo().equals(to))
                    return Optional.of(c);
            }
            return Optional.empty();
        }
    }

    @Override
    public Set<String> getCities() {
        return new HashSet<>(cities);
    }

    @Override
    public Set<String> getDestiniesFromCity(String city) {
        List<Connection> cityConnections = fromConnections.get(city);
        Set<String> result = new HashSet<>();
        if (cityConnections == null) {
            return result;
        } else {
            cityConnections.stream()
                    .map(Connection::getTo)
                    .forEach(result::add);
            return result;
        }
    }
}
