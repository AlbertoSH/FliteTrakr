package com.github.albertosh.flitetrakr.reactive.services;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

public class ConnectionServiceError extends Throwable {

    private final ErrorType errorType;

    private ConnectionServiceError(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public static ConnectionServiceError connectionNotFound() {
        String errorMessage = LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND);
        return new ConnectionServiceError(errorMessage,
                ErrorType.CONNECTION_NOT_FOUND);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        CONNECTION_NOT_FOUND
    }

}
