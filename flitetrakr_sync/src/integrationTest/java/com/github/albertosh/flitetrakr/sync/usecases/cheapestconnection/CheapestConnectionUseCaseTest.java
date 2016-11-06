package com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.ConnectionService;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class CheapestConnectionUseCaseTest {

    private IConnectionService service;
    private CheapestConnectionUseCase cheapestConnectionUseCase;

    @Before
    public void setUp() throws Exception {
        service = new ConnectionService();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(service);

        service.addConnection(new Connection("NUE-FRA-43"));
        service.addConnection(new Connection("NUE-AMS-67"));
        service.addConnection(new Connection("FRA-AMS-17"));
        service.addConnection(new Connection("FRA-LHR-27"));
        service.addConnection(new Connection("LHR-NUE-23"));
    }

    @Test
    public void existingConnection() throws Exception, CheapestConnectionUseCaseError {
        // Given

        // When
        CheapestConnectionUseCaseInput input = new CheapestConnectionUseCaseInput.Builder()
                .from("NUE")
                .to("AMS")
                .build();
        CheapestConnectionUseCaseOutput result = cheapestConnectionUseCase.execute(input);

        // Then
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
        CheapestConnectionUseCaseOutput result = cheapestConnectionUseCase.execute(input);

        // Then
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

        CheapestConnectionUseCaseError error = null;
        try {
            cheapestConnectionUseCase.execute(input);
        } catch (CheapestConnectionUseCaseError e) {
            error = e;
        }
        // Then
        assertThat(error, is(not(nullValue())));
        assertThat(error.getErrorType(),
                is(CheapestConnectionUseCaseError.ErrorType.CONNECTION_NOT_FOUND));
    }

}