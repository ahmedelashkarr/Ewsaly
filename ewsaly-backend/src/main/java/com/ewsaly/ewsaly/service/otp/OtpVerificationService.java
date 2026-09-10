package com.ewsaly.ewsaly.service.otp;

import com.ewsaly.ewsaly.models.OtpVerification;

public interface OtpVerificationService {

    OtpVerification createOtp(String phoneNumber, String otpCode, int expirationMinutes);

    OtpVerification createOtp(String phoneNumber, String otpCode);

    boolean verifyOtp(String phoneNumber, String otpCode);
}
