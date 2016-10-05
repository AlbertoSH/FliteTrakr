package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.MockService;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.CheapestConnectionUseCaseOutput;
import com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection.ICheapestConnectionUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionsBelowPriceUseCaseTest {

    private IConnectionService service;
    private ICheapestConnectionUseCase cheapestConnectionUseCase;
    private ConnectionsBelowPriceUseCase connectionsBelowPriceUseCase;

    @Before
    public void setUp() throws Exception {
        service = MockService.doMock();
        cheapestConnectionUseCase = mock(ICheapestConnectionUseCase.class);
        connectionsBelowPriceUseCase = new ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service);
    }

    @Test
    public void existingConnections() throws Exception, CheapestConnectionUseCaseError, ConnectionsBelowPriceUseCaseError {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 170;

        // Force that at least a cheap connection exists
        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(new CheapestConnectionUseCaseOutput.Builder()
                        .price(0)
                        .cities(new ArrayList<>())
                        .build());

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
        final Integer PRICE = 170;

        // Force that the cheapest connection is more expensive than the min price
        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(new CheapestConnectionUseCaseOutput.Builder()
                        .price(PRICE + 10)
                        .cities(new ArrayList<>())
                        .build());

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

        // Force that there is no connection
        when(cheapestConnectionUseCase.execute(any()))
                .thenThrow(CheapestConnectionUseCaseError.connectionNotFound());

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

        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(new CheapestConnectionUseCaseOutput.Builder()
                        .price(0)
                        .cities(new ArrayList<>())
                        .build());

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