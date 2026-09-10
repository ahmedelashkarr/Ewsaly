package com.ewsaly.ewsaly.service.scanEvent;

import com.ewsaly.ewsaly.dto.scanEvent.CreateScanEventRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventQueryRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface ScanEventService {
    ScanEventResponse recordScanEvent(String publicToken, CreateScanEventRequest request);

    Page<ScanEventResponse> getScansByQrCode(Long userId, Long qrCodeId, ScanEventQueryRequest request);

    long countByQrCodeId(Long userId, Long qrCodeId);

    long countByQrCodeIdAndScannedAtAfter(Long userId, Long qrCodeId, LocalDateTime after);

    long getScanStats(Long userId, Long qrCodeId, LocalDateTime after);

    long countByIpAddressHashAndScannedAtAfter(String ipAddressHash, LocalDateTime after);

    Page<ScanEventResponse> getUserScans(Long userId, ScanEventQueryRequest request);
}
