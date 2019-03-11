package com.sellsuki.homework.exceptions;

import com.sellsuki.homework.types.ErrorType;

public class InternalServerErrorException extends Exception {

    private ErrorType errorType;

    public InternalServerErrorException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
