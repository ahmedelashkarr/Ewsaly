package com.ewsaly.ewsaly.service.otp;

import com.ewsaly.ewsaly.exceptions.MaxOtpAttemptsExceededException;
import com.ewsaly.ewsaly.models.OtpVerification;
import com.ewsaly.ewsaly.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OtpVerificationServiceImpl implements OtpVerificationService {

    private final OtpVerificationRepository otpVerificationRepository;
    private static final int DEFAULT_EXPIRATION_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    @Transactional
    public OtpVerification createOtp(String phoneNumber, String otpCode, int expirationMinutes) {
        OtpVerification otpVerification = OtpVerification.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .isVerified(false)
                .attempts(0)
                .build();

        return otpVerificationRepository.save(otpVerification);
    }

    @Override
    @Transactional
    public OtpVerification createOtp(String phoneNumber, String otpCode) {
        return createOtp(phoneNumber, otpCode, DEFAULT_EXPIRATION_MINUTES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository
                .findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber);

        if (otpVerificationOpt.isEmpty()) {
            return false;
        }

        OtpVerification otpVerification = otpVerificationOpt.get();

        if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
            throw new MaxOtpAttemptsExceededException("error.auth.otp.max.attempts.exceeded");
        }

        otpVerification.setAttempts(otpVerification.getAttempts() + 1);

        if (otpVerification.getOtpCode().equals(otpCode) && otpVerification.getExpiresAt().isAfter(LocalDateTime.now())) {
            otpVerification.setIsVerified(true);
            otpVerificationRepository.save(otpVerification);
            return true;
        }

        otpVerificationRepository.save(otpVerification);

        return false;
    }


}
