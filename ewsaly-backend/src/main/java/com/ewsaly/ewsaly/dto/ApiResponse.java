package com.ewsaly.ewsaly.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        String message,
        T data,
        LocalDateTime timestamp) {
}
