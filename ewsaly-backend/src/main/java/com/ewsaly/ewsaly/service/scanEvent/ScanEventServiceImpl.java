package com.ewsaly.ewsaly.service.scanEvent;

import com.ewsaly.ewsaly.dto.scanEvent.CreateScanEventRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventQueryRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventResponse;
import com.ewsaly.ewsaly.exceptions.QrCodeNotFoundException;
import com.ewsaly.ewsaly.exceptions.ScanLimitExceededException;
import com.ewsaly.ewsaly.mapper.ScanEventMapper;
import com.ewsaly.ewsaly.models.QrCode;
import com.ewsaly.ewsaly.models.ScanEvent;
import com.ewsaly.ewsaly.repository.QrCodeRepository;
import com.ewsaly.ewsaly.repository.ScanEventRepository;
import com.ewsaly.ewsaly.utils.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScanEventServiceImpl implements ScanEventService {

    private static final int MAX_SCANS_PER_IP_PER_HOUR = 30;

    private final ScanEventRepository scanEventRepository;
    private final QrCodeRepository qrCodeRepository;
    private final ScanEventMapper scanEventMapper;
    private final HashUtils hashUtils;

    @Override
    @Transactional
    public ScanEventResponse recordScanEvent(String publicToken, CreateScanEventRequest request) {
        QrCode qrCode = qrCodeRepository.findByPublicToken(publicToken)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        if (!qrCode.getIsActive()) {
            throw new QrCodeNotFoundException("error.qrcode.not_active");
        }

        String ipHash = hashUtils.sha256(request.getIpAddress());

        if (ipHash != null && !ipHash.isBlank()) {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long recentIpScans = countByIpAddressHashAndScannedAtAfter(ipHash, oneHourAgo);
            if (recentIpScans >= MAX_SCANS_PER_IP_PER_HOUR) {
                throw new ScanLimitExceededException("ratelimit.scan.per_ip");
            }
        }

        ScanEvent scanEvent = ScanEvent.builder()
                .qrCode(qrCode)
                .ipAddressHash(ipHash)
                .userAgent(request.getUserAgent())
                .build();

        ScanEvent savedEvent = scanEventRepository.save(scanEvent);

        return scanEventMapper.toResponse(savedEvent);
    }

    @Override
    public Page<ScanEventResponse> getScansByQrCode(Long userId, Long qrCodeId, ScanEventQueryRequest request) {
        verifyQrCodeOwnership(userId, qrCodeId);

        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("scannedAt").descending());

        Page<ScanEvent> scanPage;
        if (request.getAfter() != null) {
            scanPage = scanEventRepository.findAllByQrCodeIdAndScannedAtAfter(qrCodeId, request.getAfter(), pageable);
        } else {
            scanPage = scanEventRepository.findAllByQrCodeId(qrCodeId, pageable);
        }

        return scanPage.map(scanEventMapper::toResponse);
    }

    @Override
    public long countByQrCodeId(Long userId, Long qrCodeId) {
        verifyQrCodeOwnership(userId, qrCodeId);
        return scanEventRepository.countByQrCodeId(qrCodeId);
    }

    @Override
    public long countByQrCodeIdAndScannedAtAfter(Long userId, Long qrCodeId, LocalDateTime after) {
        verifyQrCodeOwnership(userId, qrCodeId);
        if (after == null) {
            return 0L;
        }
        return scanEventRepository.countByQrCodeIdAndScannedAtAfter(qrCodeId, after);
    }

    @Override
    public long getScanStats(Long userId, Long qrCodeId, LocalDateTime after) {
        if (after != null) {
            return countByQrCodeIdAndScannedAtAfter(userId, qrCodeId, after);
        }
        return countByQrCodeId(userId, qrCodeId);
    }

    @Override
    public long countByIpAddressHashAndScannedAtAfter(String ipAddressHash, LocalDateTime after) {
        if (ipAddressHash == null || after == null) {
            return 0L;
        }
        return scanEventRepository.countByIpAddressHashAndScannedAtAfter(ipAddressHash, after);
    }

    @Override
    public Page<ScanEventResponse> getUserScans(Long userId, ScanEventQueryRequest request) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("scannedAt").descending());

        return scanEventRepository.findAllByQrCodeUserId(userId, pageable)
                .map(scanEventMapper::toResponse);
    }

    private void verifyQrCodeOwnership(Long userId, Long qrCodeId) {
        if (!qrCodeRepository.existsByIdAndUserId(qrCodeId, userId)) {
            throw new QrCodeNotFoundException("error.qrcode.not_found");
        }
    }
}
