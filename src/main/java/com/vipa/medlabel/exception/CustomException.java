package com.vipa.medlabel.exception;

public class CustomException extends RuntimeException {
    private final CustomError error;

    public CustomException(CustomError error) {
        super(error.getMessage());
        this.error = error;
    }

    public CustomError getErrorCode() {
        return error;
    }
}
