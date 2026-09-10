package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.qrcode.CreateQrCodeRequest;
import com.ewsaly.ewsaly.dto.qrcode.PublicQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.UpdateQrCodeRequest;
import com.ewsaly.ewsaly.dto.qrcode.UserQrCodeResponse;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import com.ewsaly.ewsaly.service.qrCode.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/qr-codes")
@RequiredArgsConstructor
@Tag(name = "QR Codes", description = "Endpoints for managing and scanning QR Codes")
public class QrCodeController {

    private final QrCodeService qrCodeService;
    private final MessageSource messageSource;

    @Operation(summary = "Create QR Code", description = "Creates a new QR Code for the authenticated user (Max free limit: 5).")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserQrCodeResponse>> createQrCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateQrCodeRequest request,
            Locale locale) {

        UserQrCodeResponse result = qrCodeService.createQrCode(userDetails.getId(), request);

        String message = messageSource.getMessage("qrcode.add.success", null, locale);
        ApiResponse<UserQrCodeResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get User QR Codes", description = "Retrieves QR Codes belonging to the authenticated user. Optionally filter by active status.")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<UserQrCodeResponse>>> getUserQrCodes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Boolean active,
            Locale locale) {

        List<UserQrCodeResponse> list = qrCodeService.getUserQrCodes(userDetails.getId(), active);

        String message = messageSource.getMessage("qrcode.fetch.success", null, locale);
        ApiResponse<List<UserQrCodeResponse>> response = new ApiResponse<>(message, list, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get QR Code By ID", description = "Retrieves a specific QR Code owned by the authenticated user.")
    @GetMapping("/{qrCodeId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserQrCodeResponse>> getQrCodeById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            Locale locale) {

        UserQrCodeResponse result = qrCodeService.getQrCodeById(userDetails.getId(), qrCodeId);

        String message = messageSource.getMessage("qrcode.fetch.success", null, locale);
        ApiResponse<UserQrCodeResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Scan / Get Public QR Code", description = "Public endpoint to retrieve active QR Code details by public token when scanned.")
    @GetMapping("/public/{publicToken}")
    public ResponseEntity<ApiResponse<PublicQrCodeResponse>> getQrCodeByPublicToken(
            @PathVariable String publicToken,
            Locale locale) {

        PublicQrCodeResponse result = qrCodeService.getQrCodeByPublicToken(publicToken);

        String message = messageSource.getMessage("qrcode.public.fetch.success", null, locale);
        ApiResponse<PublicQrCodeResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update QR Code", description = "Updates active status and/or styling design for a specific QR Code.")
    @PutMapping("/{qrCodeId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserQrCodeResponse>> updateQrCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            @RequestBody @Valid UpdateQrCodeRequest request,
            Locale locale) {

        UserQrCodeResponse result = qrCodeService.updateQrCode(userDetails.getId(), qrCodeId, request);

        String message = messageSource.getMessage("qrcode.update.success", null, locale);
        ApiResponse<UserQrCodeResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle Active Status", description = "Flips the active/inactive status of a specific QR Code.")
    @PatchMapping("/{qrCodeId}/toggle-active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserQrCodeResponse>> toggleQrCodeActiveStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            Locale locale) {

        UserQrCodeResponse result = qrCodeService.toggleQrCodeActiveStatus(userDetails.getId(), qrCodeId);

        String message = messageSource.getMessage("qrcode.toggle.success", null, locale);
        ApiResponse<UserQrCodeResponse> response = new ApiResponse<>(message, result, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete QR Code", description = "Deletes a specific QR Code owned by the authenticated user.")
    @DeleteMapping("/{qrCodeId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQrCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long qrCodeId,
            Locale locale) {

        qrCodeService.deleteQrCode(userDetails.getId(), qrCodeId);

        String message = messageSource.getMessage("qrcode.delete.success", null, locale);
        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
