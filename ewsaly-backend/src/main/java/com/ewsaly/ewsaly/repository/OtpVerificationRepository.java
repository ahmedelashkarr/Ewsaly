package com.ewsaly.ewsaly.repository;

import com.ewsaly.ewsaly.models.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);

    @Modifying
    @Query("UPDATE OtpVerification o SET o.attempts = o.attempts + 1 WHERE o.id = :id")
    void incrementAttemptsById(@Param("id") Long id);

}
