package com.github.albertosh.flitetrakr.sync.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.ConnectionService;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop.ConnectionWithExactStopUseCase;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class ConnectionWithMaximumStopUseCaseTest {

    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;
    private ConnectionWithMaximumStopUseCase connectionWithMaximumStopUseCase;

    @Before
    public void setUp() throws Exception {
        IConnectionService service = new ConnectionService();
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(service);
        connectionWithMaximumStopUseCase = new ConnectionWithMaximumStopUseCase(connectionWithExactStopUseCase);

        service.addConnection(Connection.fromString("NUE-FRA-43"));
        service.addConnection(Connection.fromString("NUE-AMS-67"));
        service.addConnection(Connection.fromString("FRA-AMS-17"));
        service.addConnection(Connection.fromString("FRA-LHR-27"));
        service.addConnection(Connection.fromString("LHR-NUE-23"));
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

        Integer result = connectionWithMaximumStopUseCase.execute(input);
        assertThat(result, is(2));
    }

}