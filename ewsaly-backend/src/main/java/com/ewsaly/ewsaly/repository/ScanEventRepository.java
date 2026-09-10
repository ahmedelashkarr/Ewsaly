package com.ewsaly.ewsaly.repository;

import com.ewsaly.ewsaly.models.ScanEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ScanEventRepository extends JpaRepository<ScanEvent, Long> {

    Page<ScanEvent> findAllByQrCodeId(Long qrCodeId, Pageable pageable);
    Page<ScanEvent> findAllByQrCodeIdAndScannedAtAfter(Long qrCodeId,
                                                       LocalDateTime after,
                                                       Pageable pageable);
    long countByQrCodeId(Long qrCodeId);

    long countByQrCodeIdAndScannedAtAfter(Long qrCodeId, LocalDateTime after);

    long countByIpAddressHashAndScannedAtAfter(String ipAddressHash, LocalDateTime after);


    Page<ScanEvent> findAllByQrCodeUserId(Long userId, Pageable pageable);
}
