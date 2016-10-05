package com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

public class CheapestConnectionUseCaseError extends Throwable {

    private final ErrorType errorType;

    private CheapestConnectionUseCaseError(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public static CheapestConnectionUseCaseError connectionNotFound() {
        String errorMessage = LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND);
        return new CheapestConnectionUseCaseError(errorMessage,
                ErrorType.CONNECTION_NOT_FOUND);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        CONNECTION_NOT_FOUND
    }
}
