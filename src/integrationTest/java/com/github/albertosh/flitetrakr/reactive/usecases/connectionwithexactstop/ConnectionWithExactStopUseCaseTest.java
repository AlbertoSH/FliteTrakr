package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionWithExactStopUseCaseTest {

    private IConnectionService service;
    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;


    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(service);

        Observable.merge(
                service.addConnection(Connection.fromString("NUE-FRA-43")),
                service.addConnection(Connection.fromString("NUE-AMS-67")),
                service.addConnection(Connection.fromString("FRA-AMS-17")),
                service.addConnection(Connection.fromString("FRA-LHR-27")),
                service.addConnection(Connection.fromString("LHR-NUE-23")))
                .subscribe();
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