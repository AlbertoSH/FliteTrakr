package com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class PriceOfConnectionUseCaseTest {

    private IConnectionService service;
    private PriceOfConnectionUseCase priceOfConnectionUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        priceOfConnectionUseCase = new PriceOfConnectionUseCase(service);

        Observable.merge(
                service.addConnection(Connection.fromString("NUE-FRA-43")),
                service.addConnection(Connection.fromString("NUE-AMS-67")),
                service.addConnection(Connection.fromString("FRA-AMS-17")),
                service.addConnection(Connection.fromString("FRA-LHR-27")),
                service.addConnection(Connection.fromString("LHR-NUE-23")))
                .subscribe();
    }

    @Test
    public void nonExistentConnectionMakesExecuteThrowAnError() throws Exception {
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode("NUE")
                .addCode("DBX")
                .build();

        TestSubscriber<Integer> observer = TestSubscriber.create();
        priceOfConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        observer.assertError(PriceOfConnectionUseCaseError.connectionNotFound());
    }

    @Test
    public void simpleConnectionReturnsThePrice() throws Exception {
        final String FROM = "NUE";
        final String TO = "FRA";
        final Integer PRICE = 43;
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode(FROM)
                .addCode(TO)
                .build();

        TestSubscriber<Integer> observer = TestSubscriber.create();
        priceOfConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        observer.assertValue(PRICE);
        observer.assertCompleted();
    }

    @Test
    public void multipleConnectionReturnsThePrice() throws Exception {
        final String FROM = "NUE";
        final String MIDDLE = "FRA";
        final String TO = "LHR";
        final Integer PRICE = 70;
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode(FROM)
                .addCode(MIDDLE)
                .addCode(TO)
                .build();

        TestSubscriber<Integer> observer = TestSubscriber.create();
        priceOfConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        observer.assertValue(PRICE);
        observer.assertCompleted();
    }
}