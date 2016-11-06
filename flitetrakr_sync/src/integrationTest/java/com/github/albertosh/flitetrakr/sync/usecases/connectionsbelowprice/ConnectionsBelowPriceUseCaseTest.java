package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.sync.services.ConnectionService;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCase;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.ICheapestConnectionUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionsBelowPriceUseCaseTest {

    private IConnectionService service;
    private ICheapestConnectionUseCase cheapestConnectionUseCase;
    private ConnectionsBelowPriceUseCase connectionsBelowPriceUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(service);
        connectionsBelowPriceUseCase = new ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service);

        service.addConnection(new Connection("NUE-FRA-43"));
        service.addConnection(new Connection("NUE-AMS-67"));
        service.addConnection(new Connection("FRA-AMS-17"));
        service.addConnection(new Connection("FRA-LHR-27"));
        service.addConnection(new Connection("LHR-NUE-23"));
    }

    @Test
    public void existingConnections() throws Exception, CheapestConnectionUseCaseError, ConnectionsBelowPriceUseCaseError {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 170;

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        List<MultipleConnections> output = connectionsBelowPriceUseCase.execute(input);

        assertThat(output, hasSize(2));

        assertThat(output.get(0).getCities(), contains("NUE", "FRA", "LHR"));
        assertThat(output.get(0).getPrice(), is(70));

        assertThat(output.get(1).getCities(), contains("NUE", "FRA", "LHR", "NUE", "FRA", "LHR"));
        assertThat(output.get(1).getPrice(), is(163));
    }

    @Test
    public void whenTheCheapestConnectionIsOverMinPriceTheAlgorithmHalts() throws Exception, CheapestConnectionUseCaseError, ConnectionsBelowPriceUseCaseError {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 60;

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        List<MultipleConnections> output = connectionsBelowPriceUseCase.execute(input);

        assertThat(output, hasSize(0));
    }

    @Test
    public void aNonExistingConnectionsThrowsAnError() throws Exception, CheapestConnectionUseCaseError {
        final String FROM = "NUE";
        final String TO = "DBX";
        final Integer PRICE = 1000;

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();

        ConnectionsBelowPriceUseCaseError error = null;
        try {
            connectionsBelowPriceUseCase.execute(input);
        } catch (ConnectionsBelowPriceUseCaseError connectionsBelowPriceUseCaseError) {
            error = connectionsBelowPriceUseCaseError;
        }

        assertThat(error, is(not(nullValue())));
        assertThat(error.getErrorType(), is(ConnectionsBelowPriceUseCaseError.ErrorType.CONNECTION_NOT_FOUND));
    }

    @Test
    public void sameFromAndTo() throws Exception, CheapestConnectionUseCaseError, ConnectionsBelowPriceUseCaseError {
        final String FROM = "LHR";
        final String TO = "LHR";
        final Integer PRICE = 100;

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        List<MultipleConnections> output = connectionsBelowPriceUseCase.execute(input);

        assertThat(output, hasSize(2));

        assertThat(output.get(0).getCities(), contains("LHR"));
        assertThat(output.get(0).getPrice(), is(0));

        assertThat(output.get(1).getCities(), contains("LHR", "NUE", "FRA", "LHR"));
        assertThat(output.get(1).getPrice(), is(93));
    }
}