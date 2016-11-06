package com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection;

import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.MockService;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

public class PriceOfConnectionUseCaseTest {

    private IConnectionService service;
    private PriceOfConnectionUseCase priceOfConnectionUseCase;

    @Before
    public void setUp() throws Exception {
        service = MockService.doMock();
        priceOfConnectionUseCase = new PriceOfConnectionUseCase(service);
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