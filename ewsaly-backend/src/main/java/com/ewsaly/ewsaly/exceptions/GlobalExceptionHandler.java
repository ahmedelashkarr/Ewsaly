package com.ewsaly.ewsaly.exceptions;

import com.ewsaly.ewsaly.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String getLocalizedMessage(String keyOrMessage, Locale locale, Object[] args) {
        if (keyOrMessage == null) {
            return messageSource.getMessage("error.internal_error", null, locale);
        }
        try {
            return messageSource.getMessage(keyOrMessage, args, locale);
        } catch (NoSuchMessageException e) {
            return keyOrMessage;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, Locale locale) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String errorMessage = getLocalizedMessage(fieldError.getDefaultMessage(), locale, null);
            errors.put(fieldError.getField(), errorMessage);
        }

        String mainMessage = getLocalizedMessage("error.validation_failed", locale, null);
        ApiResponse<Map<String, String>> response = new ApiResponse<>(mainMessage, errors, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            PhoneNumberAlreadyExistsException.class,
            AccountAlreadyVerifiedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleConflictExceptions(RuntimeException ex, Locale locale) {
        String message = getLocalizedMessage(ex.getMessage(), locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({
            InvalidOtpException.class,
            MaxOtpAttemptsExceededException.class,
            UserNotVerifiedException.class,
            MaxQrCodeLimitExceededException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestExceptions(RuntimeException ex, Locale locale) {
        String message = getLocalizedMessage(ex.getMessage(), locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ScanLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooManyRequestsExceptions(ScanLimitExceededException ex, Locale locale) {
        String message = getLocalizedMessage(ex.getMessage(), locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @ExceptionHandler({
            PhoneNumberNotFoundException.class,
            EmergencyContactNotFoundException.class,
            QrCodeNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFoundExceptions(RuntimeException ex, Locale locale) {
        String message = getLocalizedMessage(ex.getMessage(), locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex, Locale locale) {
        String message = getLocalizedMessage("auth.login.bad_credentials", locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex, Locale locale) {
        String message = getLocalizedMessage("error.unauthorized", locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        String message = getLocalizedMessage("error.forbidden", locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtExceptions(Exception ex, Locale locale) {
        log.error("Unhandled exception caught: ", ex);
        String message = getLocalizedMessage("error.internal_error", locale, null);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
