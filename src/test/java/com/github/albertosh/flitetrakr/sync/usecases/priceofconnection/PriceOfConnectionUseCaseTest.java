package com.github.albertosh.flitetrakr.sync.usecases.priceofconnection;

import com.github.albertosh.flitetrakr.sync.services.IConnectionService;
import com.github.albertosh.flitetrakr.sync.usecases.MockService;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PriceOfConnectionUseCaseTest {

    private IConnectionService service;
    private PriceOfConnectionUseCase priceOfConnectionUseCase;

    @Before
    public void setUp() throws Exception {
        service = MockService.doMock();
        priceOfConnectionUseCase = new PriceOfConnectionUseCase(service);
    }

    @Test
    public void nonExistentConnectionMakesExecuteThrowAnError() throws Exception {
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode("NUE")
                .addCode("DBX")
                .build();

        PriceOfConnectionUseCaseError priceOfConnectionUseCaseError = null;
        try {
            priceOfConnectionUseCase.execute(input);
        } catch (PriceOfConnectionUseCaseError e) {
            priceOfConnectionUseCaseError = e;
        }
        // Then
        assertThat(priceOfConnectionUseCaseError, is(not(nullValue())));
        assertThat(priceOfConnectionUseCaseError.getErrorType(),
                is(PriceOfConnectionUseCaseError.ErrorType.CONNECTION_NOT_FOUND));
    }

    @Test
    public void simpleConnectionReturnsThePrice() throws Exception {
        final String FROM = "NUE";
        final String TO = "FRA";
        final Integer PRICE = 43;
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode(FROM)
                .addCode(TO)
                .build();

        Integer result = null;

        try {
            result = priceOfConnectionUseCase.execute(input);
        } catch (PriceOfConnectionUseCaseError e) {
            throw new RuntimeException(e);
        }
        // Then
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(PRICE));
    }

    @Test
    public void multipleConnectionReturnsThePrice() throws Exception {
        final String FROM = "NUE";
        final String MIDDLE = "FRA";
        final String TO = "LHR";
        final Integer PRICE = 70;
        // Given

        // When
        PriceOfConnectionUseCaseInput input = new PriceOfConnectionUseCaseInput.Builder()
                .addCode(FROM)
                .addCode(MIDDLE)
                .addCode(TO)
                .build();

        Integer result = null;

        try {
            result = priceOfConnectionUseCase.execute(input);
        } catch (PriceOfConnectionUseCaseError e) {
            throw new RuntimeException(e);
        }
        // Then
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(PRICE));
    }
}