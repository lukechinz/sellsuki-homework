package com.sellsuki.homework.exceptions;

import com.sellsuki.homework.types.ErrorType;

public class BadRequestException extends Exception {

    private ErrorType errorType;

    public BadRequestException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}