package com.ewsaly.ewsaly.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String token;
    private Boolean isProfileCompleted;
    private LocalDateTime timestamp;
}