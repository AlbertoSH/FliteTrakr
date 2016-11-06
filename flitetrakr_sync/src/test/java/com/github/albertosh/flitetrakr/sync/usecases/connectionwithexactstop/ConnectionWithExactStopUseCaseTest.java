package com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop;

import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.MockService;

import org.junit.Before;
import org.junit.Test;

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
        Integer result = connectionWithExactStopUseCase.execute(input);

        // Then
        assertThat(result, is(1));
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
        Integer result = connectionWithExactStopUseCase.execute(input);

        // Then
        assertThat(result, is(1));
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
        Integer result = connectionWithExactStopUseCase.execute(input);

        // Then
        assertThat(result, is(0));
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
        Integer result = connectionWithExactStopUseCase.execute(input);

        // Then
        assertThat(result, is(0));
    }
}