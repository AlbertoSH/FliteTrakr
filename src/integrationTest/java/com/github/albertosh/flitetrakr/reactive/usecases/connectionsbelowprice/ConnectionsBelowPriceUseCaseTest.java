package com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseError;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.ICheapestConnectionUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class ConnectionsBelowPriceUseCaseTest {

    private IConnectionService service;
    private ICheapestConnectionUseCase cheapestConnectionUseCase;
    private ConnectionsBelowPriceUseCase connectionsBelowPriceUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(service);
        connectionsBelowPriceUseCase = new ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service);

        Observable.merge(
                service.addConnection(Connection.fromString("NUE-FRA-43")),
                service.addConnection(Connection.fromString("NUE-AMS-67")),
                service.addConnection(Connection.fromString("FRA-AMS-17")),
                service.addConnection(Connection.fromString("FRA-LHR-27")),
                service.addConnection(Connection.fromString("LHR-NUE-23")))
                .subscribe();
    }

    @Test
    public void existingConnections() throws Exception {
        final String FROM = "NUE";
        final String TO = "LHR";
        final Integer PRICE = 170;

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
        final Integer PRICE = 10;

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