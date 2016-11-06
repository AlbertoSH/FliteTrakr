package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCase;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionWithMaximumStopUseCaseTest {

    private IConnectionService service;
    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;
    private ConnectionWithMaximumStopUseCase connectionWithMaximumStopUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(service);
        connectionWithMaximumStopUseCase = new ConnectionWithMaximumStopUseCase(connectionWithExactStopUseCase);

        Observable.merge(
                service.addConnection(new Connection("NUE-FRA-43")),
                service.addConnection(new Connection("NUE-AMS-67")),
                service.addConnection(new Connection("FRA-AMS-17")),
                service.addConnection(new Connection("FRA-LHR-27")),
                service.addConnection(new Connection("LHR-NUE-23")))
                .subscribe();
    }

    @Test
    public void execute() throws Exception {
        final String FROM = "NUE";
        final String TO = "FRA";
        final Integer STOPS = 3;
        ConnectionWithMaximumStopUseCaseInput input = new ConnectionWithMaximumStopUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .stops(STOPS)
                .build();

        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithMaximumStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(2));
    }

}