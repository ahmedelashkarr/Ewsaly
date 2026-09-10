package com.ewsaly.ewsaly.exceptions;

public class ScanLimitExceededException extends RuntimeException {
    public ScanLimitExceededException(String message) {
        super(message);
    }
}
