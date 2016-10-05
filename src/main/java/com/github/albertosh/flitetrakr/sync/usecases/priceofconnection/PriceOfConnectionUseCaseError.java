package com.github.albertosh.flitetrakr.sync.usecases.priceofconnection;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

public class PriceOfConnectionUseCaseError extends Throwable {

    private final ErrorType errorType;

    private PriceOfConnectionUseCaseError(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public static PriceOfConnectionUseCaseError connectionNotFound() {
        String errorMessage = LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND);
        return new PriceOfConnectionUseCaseError(errorMessage,
                ErrorType.CONNECTION_NOT_FOUND);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        CONNECTION_NOT_FOUND
    }
}
