package com.ewsaly.ewsaly.service.otp;

import com.ewsaly.ewsaly.exceptions.MaxOtpAttemptsExceededException;
import com.ewsaly.ewsaly.models.OtpVerification;
import com.ewsaly.ewsaly.repository.OtpVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpVerificationServiceImplTest {

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @InjectMocks
    private OtpVerificationServiceImpl otpVerificationService;

    private String phoneNumber;
    private String validOtpCode;
    private OtpVerification otpVerification;

    @BeforeEach
    void setUp() {
        phoneNumber = "+201012345678";
        validOtpCode = "123456";
        otpVerification = OtpVerification.builder()
                .id(1L)
                .phoneNumber(phoneNumber)
                .otpCode(validOtpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isVerified(false)
                .attempts(0)
                .build();
    }

    @Test
    @DisplayName("Should return false when no pending OTP is found")
    void verifyOtp_WhenOtpNotFound_ReturnsFalse() {
        when(otpVerificationRepository.findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber))
                .thenReturn(Optional.empty());

        boolean result = otpVerificationService.verifyOtp(phoneNumber, validOtpCode);

        assertFalse(result);
        verify(otpVerificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw MaxOtpAttemptsExceededException when attempts >= 3")
    void verifyOtp_WhenAttemptsExceedMax_ThrowsException() {
        otpVerification.setAttempts(3);
        when(otpVerificationRepository.findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber))
                .thenReturn(Optional.of(otpVerification));

        assertThrows(MaxOtpAttemptsExceededException.class, () ->
                otpVerificationService.verifyOtp(phoneNumber, validOtpCode)
        );

        verify(otpVerificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true and set verified to true when OTP is valid and not expired")
    void verifyOtp_WhenValidAndNotExpired_ReturnsTrue() {
        when(otpVerificationRepository.findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber))
                .thenReturn(Optional.of(otpVerification));

        boolean result = otpVerificationService.verifyOtp(phoneNumber, validOtpCode);

        assertTrue(result);
        assertTrue(otpVerification.getIsVerified());
        assertEquals(1, otpVerification.getAttempts());
        verify(otpVerificationRepository, times(1)).save(otpVerification);
    }

    @Test
    @DisplayName("Should increment attempts and return false when OTP code is incorrect")
    void verifyOtp_WhenCodeInvalid_ReturnsFalse() {
        when(otpVerificationRepository.findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber))
                .thenReturn(Optional.of(otpVerification));

        boolean result = otpVerificationService.verifyOtp(phoneNumber, "999999");

        assertFalse(result);
        assertFalse(otpVerification.getIsVerified());
        assertEquals(1, otpVerification.getAttempts());
        verify(otpVerificationRepository, times(1)).save(otpVerification);
    }

    @Test
    @DisplayName("Should increment attempts and return false when OTP is expired")
    void verifyOtp_WhenExpired_ReturnsFalse() {
        otpVerification.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(otpVerificationRepository.findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber))
                .thenReturn(Optional.of(otpVerification));

        boolean result = otpVerificationService.verifyOtp(phoneNumber, validOtpCode);

        assertFalse(result);
        assertFalse(otpVerification.getIsVerified());
        assertEquals(1, otpVerification.getAttempts());
        verify(otpVerificationRepository, times(1)).save(otpVerification);
    }
}
