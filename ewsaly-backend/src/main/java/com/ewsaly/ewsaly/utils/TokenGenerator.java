package com.ewsaly.ewsaly.utils;

import java.security.SecureRandom;

public final class TokenGenerator {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_LENGTH = 10;

    private TokenGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generateToken() {
        return generateToken(DEFAULT_LENGTH);
    }

    private static String generateToken(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Token length must be greater than zero");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(randomIndex));
        }
        return sb.toString();
    }
}
