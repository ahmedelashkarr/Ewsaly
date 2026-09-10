package com.ewsaly.ewsaly.exceptions;

public class QrCodeNotFoundException extends RuntimeException {
    public QrCodeNotFoundException(String message) {
        super(message);
    }
}
