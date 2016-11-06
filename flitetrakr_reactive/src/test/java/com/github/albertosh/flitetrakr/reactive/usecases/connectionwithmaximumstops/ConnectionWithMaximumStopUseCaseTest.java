package com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops;

import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCaseInput;

import org.junit.Before;
import org.junit.Test;

import rx.Single;
import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.hasSize;
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
                .thenReturn(Single.just(0));

        TestSubscriber<Integer> observer = TestSubscriber.create();
        connectionWithMaximumStopUseCase.execute(input).subscribe(observer);
        observer.awaitTerminalEvent();

        assertThat(observer.getOnNextEvents(), hasSize(1));
        assertThat(observer.getOnNextEvents().get(0), is(0));
        for (int i = 0; i < STOPS; i++) {
            verify(connectionWithExactStopUseCase).execute(new ConnectionWithExactStopUseCaseInput.Builder()
                    .from(FROM)
                    .to(TO)
                    .stops(i)
                    .build());
        }

    }

}