package com.ewsaly.ewsaly.exceptions;

public class MaxQrCodeLimitExceededException extends RuntimeException {
    public MaxQrCodeLimitExceededException(String message) {
        super(message);
    }
}
