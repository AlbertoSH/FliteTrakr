package com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection;

import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.MockService;

import org.junit.Before;
import org.junit.Test;

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
        service = MockService.doMock();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(service);
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