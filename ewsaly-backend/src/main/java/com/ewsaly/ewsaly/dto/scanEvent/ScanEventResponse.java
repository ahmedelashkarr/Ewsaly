package com.ewsaly.ewsaly.dto.scanEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanEventResponse {

    private Long id;

    private LocalDateTime scannedAt;

    private String ipAddressHash;

    private String userAgent;

    private Long qrCodeId;
}
