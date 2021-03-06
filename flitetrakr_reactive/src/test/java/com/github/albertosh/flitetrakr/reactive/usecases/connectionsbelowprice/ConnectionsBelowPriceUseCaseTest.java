package com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.MockService;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseOutput;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.ICheapestConnectionUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Single;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
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
    public void existingConnections() throws Exception {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 170;

        // Force that at least a cheap connection exists
        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(Single.just(new CheapestConnectionUseCaseOutput.Builder()
                        .price(0)
                        .cities(new ArrayList<>())
                        .build()));

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        TestSubscriber<List<MultipleConnections>> observer = TestSubscriber.create();
        connectionsBelowPriceUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(2));

        List<MultipleConnections> firstResult = observer.getOnNextEvents().get(0);
        assertThat(firstResult, hasSize(1));
        assertThat(firstResult.get(0).getCities(), contains("NUE", "FRA", "LHR"));
        assertThat(firstResult.get(0).getPrice(), is(70));

        List<MultipleConnections> secondResult = observer.getOnNextEvents().get(1);
        assertThat(secondResult, hasSize(2));
        assertThat(secondResult.get(0).getCities(), contains("NUE", "FRA", "LHR"));
        assertThat(secondResult.get(0).getPrice(), is(70));
        assertThat(secondResult.get(1).getCities(), contains("NUE", "FRA", "LHR", "NUE", "FRA", "LHR"));
        assertThat(secondResult.get(1).getPrice(), is(163));
    }

    @Test
    public void whenTheCheapestConnectionIsOverMinPriceTheAlgorithmHalts() throws Exception {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 170;

        // Force that the cheapest connection is more expensive than the min price
        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(Single.just(new CheapestConnectionUseCaseOutput.Builder()
                        .price(PRICE + 10)
                        .cities(new ArrayList<>())
                        .build()));

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        TestSubscriber<List<MultipleConnections>> observer = TestSubscriber.create();
        connectionsBelowPriceUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), hasSize(0));
    }

    @Test
    public void aNonExistingConnectionsThrowsAnError() throws Exception {
        final String FROM = "NUE";
        final String TO = "DBX";
        final Integer PRICE = 1000;

        // Force that there is no connection
        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(Single.error(CheapestConnectionUseCaseError.connectionNotFound()));

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();

        TestSubscriber<List<MultipleConnections>> observer = TestSubscriber.create();
        connectionsBelowPriceUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(0));
        assertThat(((ConnectionsBelowPriceUseCaseError) observer.getOnErrorEvents().get(0))
                .getErrorType(), is(ConnectionsBelowPriceUseCaseError.ErrorType.CONNECTION_NOT_FOUND));
    }

    @Test
    public void sameFromAndTo() throws Exception, CheapestConnectionUseCaseError, ConnectionsBelowPriceUseCaseError {
        final String FROM = "LHR";
        final String TO = "LHR";
        final Integer PRICE = 100;

        when(cheapestConnectionUseCase.execute(any()))
                .thenReturn(Single.just(new CheapestConnectionUseCaseOutput.Builder()
                        .price(0)
                        .cities(new ArrayList<>())
                        .build()));

        ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .price(PRICE)
                .build();
        TestSubscriber<List<MultipleConnections>> observer = TestSubscriber.create();
        connectionsBelowPriceUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(2));

        List<MultipleConnections> firstResult = observer.getOnNextEvents().get(0);
        assertThat(firstResult, hasSize(1));
        assertThat(firstResult.get(0).getCities(), contains("LHR"));
        assertThat(firstResult.get(0).getPrice(), is(0));

        List<MultipleConnections> secondResult = observer.getOnNextEvents().get(1);
        assertThat(secondResult, hasSize(2));
        assertThat(secondResult.get(0).getCities(), contains("LHR"));
        assertThat(secondResult.get(0).getPrice(), is(0));
        assertThat(secondResult.get(1).getCities(), contains("LHR", "NUE", "FRA", "LHR"));
        assertThat(secondResult.get(1).getPrice(), is(93));
    }
}