package com.ewsaly.ewsaly.repository;

import com.ewsaly.ewsaly.enums.ItemType;
import com.ewsaly.ewsaly.models.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByPublicToken(String publicToken);

    Optional<QrCode> findByPublicTokenAndIsActiveTrue(String publicToken);

    boolean existsByPublicToken(String publicToken);

    List<QrCode> findAllByUserId(Long userId);

    List<QrCode> findAllByUserIdAndIsActiveTrue(Long userId);

    Optional<QrCode> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}

