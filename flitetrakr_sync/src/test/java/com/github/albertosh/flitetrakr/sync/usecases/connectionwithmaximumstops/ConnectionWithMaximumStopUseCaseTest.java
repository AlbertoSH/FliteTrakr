package com.github.albertosh.flitetrakr.sync.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop.ConnectionWithExactStopUseCase;
import com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop.ConnectionWithExactStopUseCaseInput;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ConnectionWithMaximumStopUseCaseTest {

    private ConnectionWithExactStopUseCase connectionWithExactStopUseCase;
    private ConnectionWithMaximumStopUseCase connectionWithMaximumStopUseCase;

    @Before
    public void setUp() throws Exception {
        connectionWithExactStopUseCase = mock(ConnectionWithExactStopUseCase.class);
        connectionWithMaximumStopUseCase = new ConnectionWithMaximumStopUseCase(connectionWithExactStopUseCase);
    }

    @Test
    public void execute() throws Exception {
        final String FROM = "FROM";
        final String TO = "TO";
        final Integer STOPS = 5;
        ConnectionWithMaximumStopUseCaseInput input = new ConnectionWithMaximumStopUseCaseInput.Builder()
                .from(FROM)
                .to(TO)
                .stops(STOPS)
                .build();
        when(connectionWithExactStopUseCase.execute(any()))
                .thenReturn(0);

        Integer result = connectionWithMaximumStopUseCase.execute(input);

        assertThat(result, is(0));
        for (int i = 0; i < STOPS; i++) {
            verify(connectionWithExactStopUseCase).execute(new ConnectionWithExactStopUseCaseInput.Builder()
                    .from(FROM)
                    .to(TO)
                    .stops(i)
                    .build());
        }

    }

}