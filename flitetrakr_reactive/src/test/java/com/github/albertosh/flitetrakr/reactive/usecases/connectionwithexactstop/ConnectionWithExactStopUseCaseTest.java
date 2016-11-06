package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop;

import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.MockService;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionWithExactStopUseCaseTest {

    private IConnectionService service;
    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;


    @Before
    public void setUp() throws Exception {
        service = MockService.doMock();
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(service);
    }

    @Test
    public void successfulConnectionWithZeroStops() throws Exception {
        // Given

        // When
        ConnectionWithExactStopUseCaseInput input = new ConnectionWithExactStopUseCaseInput.Builder()
                .from("LHR")
                .to("NUE")
                .stops(0)
                .build();
        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithExactStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(1));
    }


    @Test
    public void successfulConnectionWithOneStop() throws Exception {
        // Given

        // When
        ConnectionWithExactStopUseCaseInput input = new ConnectionWithExactStopUseCaseInput.Builder()
                .from("NUE")
                .to("AMS")
                .stops(1)
                .build();
        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithExactStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(1));
    }

    @Test
    public void unsuccessfulConnectionWithZeroStop() throws Exception {
        // Given

        // When
        ConnectionWithExactStopUseCaseInput input = new ConnectionWithExactStopUseCaseInput.Builder()
                .from("NUE")
                .to("LHR")
                .stops(0)
                .build();
        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithExactStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(0));
    }

    @Test
    public void unsuccessfulConnectionWithTwoStop() throws Exception {
        // Given

        // When
        ConnectionWithExactStopUseCaseInput input = new ConnectionWithExactStopUseCaseInput.Builder()
                .from("NUE")
                .to("FRA")
                .stops(2)
                .build();
        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithExactStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(0));
    }
}