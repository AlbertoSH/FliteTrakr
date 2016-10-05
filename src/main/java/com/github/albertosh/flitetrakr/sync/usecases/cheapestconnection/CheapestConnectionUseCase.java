package com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.util.Pair;
import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

public class CheapestConnectionUseCase
        implements ICheapestConnectionUseCase {

    private final IConnectionService connectionService;

    public CheapestConnectionUseCase(IConnectionService connectionService) {
        this.connectionService = Preconditions.checkNotNull(connectionService);
    }

    @Override
    public CheapestConnectionUseCaseOutput execute(CheapestConnectionUseCaseInput input)
            throws CheapestConnectionUseCaseError {
        // Dijkstra's algorithm slightly modified
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> father = new HashMap<>();
        Map<String, Boolean> seen = new HashMap<>();
        Set<String> nodes = connectionService.getCities();
        nodes.forEach(city -> {
            distance.put(city, Integer.MAX_VALUE);
            father.put(city, null);
            seen.put(city, false);
        });


        PriorityQueue<Pair<String, Integer>> queue = new PriorityQueue<>((p1, p2) -> p1.second.compareTo(p2.second));

        // Modification so the same start and end matches the specification
        if (input.getFrom().equals(input.getTo())) {
            Set<String> connectedCities = connectionService.getDestiniesFromCity(input.getFrom());
            connectedCities.forEach(city -> {
                Optional<Connection> c = connectionService.recoverConnection(input.getFrom(), city);
                if (!c.isPresent())
                    throw new RuntimeException(LanguageUtils.getMessage(Message.UNKNOWN_ERROR));
                queue.add(Pair.create(c.get().getTo(), c.get().getPrice()));
                distance.put(c.get().getTo(), c.get().getPrice());
                father.put(city, input.getFrom());
            });
        } else {
            distance.put(input.getFrom(), 0);
            queue.add(Pair.create(input.getFrom(), 0));
        }

        while (!queue.isEmpty()) {
            Pair<String, Integer> minDistance = queue.poll();
            seen.put(minDistance.first, true);
            Set<String> connectedCities = connectionService.getDestiniesFromCity(minDistance.first);
            connectedCities.stream()
                    .filter(city -> !seen.get(city))
                    .filter(city -> {
                        Optional<Connection> optConnection = connectionService.recoverConnection(minDistance.first, city);
                        if (!optConnection.isPresent())
                            throw new RuntimeException(LanguageUtils.getMessage(Message.UNKNOWN_ERROR));
                        Connection connection = optConnection.get();
                        return (distance.get(city) > distance.get(minDistance.first) + connection.getPrice());
                    }).forEach(city -> {
                Optional<Connection> optConnection = connectionService.recoverConnection(minDistance.first, city);
                if (!optConnection.isPresent())
                    throw new RuntimeException(LanguageUtils.getMessage(Message.UNKNOWN_ERROR));
                Connection connection = optConnection.get();
                distance.put(city, distance.get(minDistance.first) + connection.getPrice());
                father.put(city, minDistance.first);
                queue.add(Pair.create(city, distance.get(city)));
            });
        }

        if (father.get(input.getTo()) == null) {
            throw CheapestConnectionUseCaseError.connectionNotFound();
        } else {
            List<String> citiesVisited = new ArrayList<>();
            String cityVisited = input.getTo();
            citiesVisited.add(cityVisited);
            do {
                cityVisited = father.get(cityVisited);
                citiesVisited.add(0, cityVisited);
            } while ((cityVisited != null) && (!cityVisited.equals(input.getFrom())));

            return new CheapestConnectionUseCaseOutput.Builder()
                    .cities(citiesVisited)
                    .price(distance.get(input.getTo()))
                    .build();
        }
    }


}
