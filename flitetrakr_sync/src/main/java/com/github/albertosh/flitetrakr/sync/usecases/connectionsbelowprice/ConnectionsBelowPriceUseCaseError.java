package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

public class ConnectionsBelowPriceUseCaseError extends Throwable {

    private final ErrorType errorType;

    private ConnectionsBelowPriceUseCaseError(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public static ConnectionsBelowPriceUseCaseError connectionNotFound() {
        String errorMessage = LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND);
        return new ConnectionsBelowPriceUseCaseError(errorMessage,
                ErrorType.CONNECTION_NOT_FOUND);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        CONNECTION_NOT_FOUND
    }
}
