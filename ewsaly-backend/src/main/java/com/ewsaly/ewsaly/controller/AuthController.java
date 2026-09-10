package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.auth.AuthResponse;
import com.ewsaly.ewsaly.dto.auth.LoginRequest;
import com.ewsaly.ewsaly.dto.auth.SignUpRequest;
import com.ewsaly.ewsaly.dto.auth.VerifyOtpRequest;
import com.ewsaly.ewsaly.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Locale;

import com.ewsaly.ewsaly.dto.auth.ResendOtpRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and OTP verification")
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @Operation(summary = "User Login", description = "Authenticates a user with phone number and password, returning JWT access token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request, Locale locale) {
        AuthResponse authResponse = authService.login(request);

        String message = messageSource.getMessage("auth.login.success", null, locale);

        authResponse.setMessage(message);

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "User Registration", description = "Registers a new user and sends OTP code for account verification.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid SignUpRequest request, Locale locale) {

        authService.register(request);

        String message = messageSource.getMessage("auth.register.success", null, locale);

        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Verify OTP", description = "Verifies user account using the 6-digit OTP code sent during registration.")
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request, Locale locale) {
        AuthResponse authResponse = authService.verifyOtp(request);

        String message = messageSource.getMessage("auth.verify.success", null, locale);

        authResponse.setMessage(message);

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Resend OTP", description = "Resends a new 6-digit OTP code to the registered user's phone number.")
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody @Valid ResendOtpRequest request, Locale locale) {

        authService.resendOtp(request);

        String message = messageSource.getMessage("auth.otp.resend.success", null, locale);

        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
