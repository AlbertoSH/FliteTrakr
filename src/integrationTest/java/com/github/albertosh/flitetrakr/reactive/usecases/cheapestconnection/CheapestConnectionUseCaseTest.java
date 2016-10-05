package com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;


public class CheapestConnectionUseCaseTest {

    private IConnectionService service;
    private CheapestConnectionUseCase cheapestConnectionUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(service);

        Observable.merge(
                service.addConnection(Connection.fromString("NUE-FRA-43")),
                service.addConnection(Connection.fromString("NUE-AMS-67")),
                service.addConnection(Connection.fromString("FRA-AMS-17")),
                service.addConnection(Connection.fromString("FRA-LHR-27")),
                service.addConnection(Connection.fromString("LHR-NUE-23")))
                .subscribe();
    }

    @Test
    public void existingConnection() throws Exception, CheapestConnectionUseCaseError {
        // Given

        // When
        CheapestConnectionUseCaseInput input = new CheapestConnectionUseCaseInput.Builder()
                .from("NUE")
                .to("AMS")
                .build();
        TestSubscriber<CheapestConnectionUseCaseOutput> observer = TestSubscriber.create();
        cheapestConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();


        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        CheapestConnectionUseCaseOutput result = observer.getOnNextEvents().get(0);
        assertThat(result.getCities(), contains("NUE", "FRA", "AMS"));
        assertThat(result.getPrice(), is(60));
    }

    @Test
    public void sameOriginAndDestiny() throws Exception, CheapestConnectionUseCaseError {
        // Given

        // When
        CheapestConnectionUseCaseInput input = new CheapestConnectionUseCaseInput.Builder()
                .from("LHR")
                .to("LHR")
                .build();
        TestSubscriber<CheapestConnectionUseCaseOutput> observer = TestSubscriber.create();
        cheapestConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(1));
        CheapestConnectionUseCaseOutput result = observer.getOnNextEvents().get(0);
        assertThat(result.getCities(), contains("LHR", "NUE", "FRA", "LHR"));
        assertThat(result.getPrice(), is(93));
    }

    @Test
    public void nonExistingConnection() throws Exception {
        // Given

        // When
        CheapestConnectionUseCaseInput input = new CheapestConnectionUseCaseInput.Builder()
                .from("NUE")
                .to("DXB")
                .build();

        TestSubscriber<CheapestConnectionUseCaseOutput> observer = TestSubscriber.create();
        cheapestConnectionUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        assertThat(observer.getOnNextEvents(), hasSize(0));
        assertThat(observer.getOnErrorEvents(), hasSize(1));
        CheapestConnectionUseCaseError error = (CheapestConnectionUseCaseError) observer.getOnErrorEvents().get(0);
        assertThat(error.getErrorType(),
                is(CheapestConnectionUseCaseError.ErrorType.CONNECTION_NOT_FOUND));
    }

}