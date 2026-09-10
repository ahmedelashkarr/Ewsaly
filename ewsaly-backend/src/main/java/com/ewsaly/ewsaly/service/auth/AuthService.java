package com.ewsaly.ewsaly.service.auth;

import com.ewsaly.ewsaly.dto.auth.AuthResponse;
import com.ewsaly.ewsaly.dto.auth.LoginRequest;
import com.ewsaly.ewsaly.dto.auth.SignUpRequest;
import com.ewsaly.ewsaly.dto.auth.ResendOtpRequest;
import com.ewsaly.ewsaly.dto.auth.VerifyOtpRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    void register(SignUpRequest signUpRequest);
    AuthResponse verifyOtp(VerifyOtpRequest request);
    void resendOtp(ResendOtpRequest request);
}

