package com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.ConnectionService;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionWithExactStopUseCaseTest {

    private IConnectionService service;
    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;


    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(service);

        service.addConnection(new Connection("NUE-FRA-43"));
        service.addConnection(new Connection("NUE-AMS-67"));
        service.addConnection(new Connection("FRA-AMS-17"));
        service.addConnection(new Connection("FRA-LHR-27"));
        service.addConnection(new Connection("LHR-NUE-23"));
    }

    @Test
    public void successfulConnectionWithOneStop() throws Exception {
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
    public void otherSuccessfulConnectionWithOneStop() throws Exception {
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


}