package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.scanEvent.CreateScanEventRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventQueryRequest;
import com.ewsaly.ewsaly.dto.scanEvent.ScanEventResponse;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import com.ewsaly.ewsaly.service.scanEvent.ScanEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/scan-events")
@RequiredArgsConstructor
@Tag(name = "Scan Events", description = "Endpoints for recording and retrieving QR Code scan history and statistics")
public class ScanEventController {

    private final ScanEventService scanEventService;
    private final MessageSource messageSource;

    @Operation(summary = "Record Scan Event", description = "Public endpoint triggered when a QR Code is scanned.")
    @PostMapping("/public/{publicToken}")
    public ResponseEntity<ApiResponse<ScanEventResponse>> recordScanEvent(
            @PathVariable String publicToken,
            HttpServletRequest servletRequest,
            Locale locale) {

        String clientIp = servletRequest.getRemoteAddr();

        String userAgent = servletRequest.getHeader("User-Agent");

        CreateScanEventRequest request = CreateScanEventRequest.builder()
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .build();

        ScanEventResponse result = scanEventService.recordScanEvent(publicToken, request);

        String message = messageSource.getMessage("scan.event.record.success", null, locale);

        ApiResponse<ScanEventResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Scans By QR Code", description = "Retrieves paginated scan history for a specific QR code owned by the authenticated user.")
    @GetMapping("/qr-code/{qrCodeId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ScanEventResponse>>> getScansByQrCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Locale locale) {

        ScanEventQueryRequest queryRequest = ScanEventQueryRequest.builder()
                .after(after)
                .page(page)
                .size(size)
                .build();

        Page<ScanEventResponse> result = scanEventService.getScansByQrCode(userDetails.getId(), qrCodeId, queryRequest);

        String message = messageSource.getMessage("scan.event.fetch.success", null, locale);

        ApiResponse<Page<ScanEventResponse>> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Scan Stats", description = "Retrieves total or filtered scan count for a specific QR code owned by the authenticated user.")
    @GetMapping("/qr-code/{qrCodeId}/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getScanStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            Locale locale) {

        long count = scanEventService.getScanStats(userDetails.getId(), qrCodeId, after);

        String message = messageSource.getMessage("scan.event.stats.fetch.success", null, locale);

        ApiResponse<Long> response = new ApiResponse<>(message, count, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get User Scans", description = "Retrieves all scans across all QR codes owned by the authenticated user.")
    @GetMapping("/my-scans")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ScanEventResponse>>> getUserScans(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Locale locale) {

        ScanEventQueryRequest queryRequest = ScanEventQueryRequest.builder()
                .page(page)
                .size(size)
                .build();

        Page<ScanEventResponse> result = scanEventService.getUserScans(userDetails.getId(), queryRequest);

        String message = messageSource.getMessage("scan.event.fetch.success", null, locale);

        ApiResponse<Page<ScanEventResponse>> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
