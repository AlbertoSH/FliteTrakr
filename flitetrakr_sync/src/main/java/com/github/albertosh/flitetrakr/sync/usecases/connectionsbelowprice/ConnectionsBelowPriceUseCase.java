package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCaseInput;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.ICheapestConnectionUseCase;
import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ConnectionsBelowPriceUseCase
        implements IConnectionsBelowPriceUseCase {

    private final ICheapestConnectionUseCase cheapestConnectionUseCase;
    private final IConnectionService connectionService;

    public ConnectionsBelowPriceUseCase(ICheapestConnectionUseCase cheapestConnectionUseCase, IConnectionService connectionService) {
        this.cheapestConnectionUseCase = cheapestConnectionUseCase;
        this.connectionService = connectionService;
    }

    @Override
    public List<MultipleConnections> execute(ConnectionsBelowPriceUseCaseInput input) throws ConnectionsBelowPriceUseCaseError {

        Integer cheapestConnection;
        try {
            cheapestConnection = calculateCheapestConnection(input);
        } catch (CheapestConnectionUseCaseError cheapestConnectionUseCaseError) {
            if (cheapestConnectionUseCaseError.getErrorType()
                    .equals(CheapestConnectionUseCaseError.ErrorType.CONNECTION_NOT_FOUND)) {
                throw ConnectionsBelowPriceUseCaseError.connectionNotFound();
            } else {
                // Shouldn't happen...
                // if happens while development we want to know it ASAP
                throw new RuntimeException(cheapestConnectionUseCaseError);
            }
        }

        if (cheapestConnection > input.getPrice()) {
            // The cheapest connection is more expensive than our output
            // We're done...
            return Collections.emptyList();
        } else {
            List<MultipleConnections> output = performDPSSearch(input);

            Collections.sort(output);

            return output;
        }
    }

    private Integer calculateCheapestConnection(ConnectionsBelowPriceUseCaseInput input) throws CheapestConnectionUseCaseError {
        CheapestConnectionUseCaseInput cheapestConnectionUseCaseInput = new CheapestConnectionUseCaseInput.Builder()
                .from(input.getFrom())
                .to(input.getTo())
                .build();
        return cheapestConnectionUseCase.execute(cheapestConnectionUseCaseInput)
                .getPrice();
    }

    private List<MultipleConnections> performDPSSearch(ConnectionsBelowPriceUseCaseInput input) {

        List<MultipleConnections> result = new ArrayList<>();
        MultipleConnections.Builder connectionBuilder =
                new MultipleConnections.Builder(input.getFrom());

        // Concrete case of starting and ending at the same place
        if (input.getFrom().equals(input.getTo())) {
            result.add(new MultipleConnections.Builder(input.getFrom()).build());
        }

        performDPSSearch(input.getFrom(), input.getTo(), input.getPrice(), result, connectionBuilder);

        return result;
    }

    private void performDPSSearch(String from, String to, Integer price,
                                  List<MultipleConnections> result,
                                  MultipleConnections.Builder connectionBuilder) {

        Set<String> destinies = connectionService.getDestiniesFromCity(from);
        for (String city : destinies) {
            Optional<Connection> optConnection = connectionService.recoverConnection(from, city);
            if (!optConnection.isPresent())
                throw new RuntimeException(LanguageUtils.getMessage(Message.UNKNOWN_ERROR));
            Connection connection = optConnection.get();
            Integer connectionPrice = connection.getPrice();
            if (connectionPrice <= price) {
                MultipleConnections.Builder newBuilder = connectionBuilder.clone();
                newBuilder.plusPrice(connectionPrice);
                newBuilder.withCity(city);

                if (city.equals(to))
                    result.add(newBuilder.build());

                performDPSSearch(city, to, price - connectionPrice,
                        result, newBuilder);

            }
        }
    }


}
