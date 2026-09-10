package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import com.ewsaly.ewsaly.service.emergency.EmergencyContactService;
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
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
@Tag(name = "Emergency Contacts", description = "Endpoints for managing user emergency contact numbers")
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;
    private final MessageSource messageSource;

    @Operation(summary = "Get User Emergency Contacts", description = "Retrieves all emergency contacts associated with the authenticated user.")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<EmergencyContactResponse>>> getUserEmergencyContacts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Locale locale) {
        
        List<EmergencyContactResponse> contacts = emergencyContactService.getUserEmergencyContacts(userDetails.getId());
        
        String message = messageSource.getMessage("emergency.contact.fetch.success", null, locale);
        ApiResponse<List<EmergencyContactResponse>> response = new ApiResponse<>(message, contacts, LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add Emergency Contact", description = "Adds a new emergency contact for the authenticated user.")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmergencyContactResponse>> addEmergencyContact(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateEmergencyContactRequest request,
            Locale locale) {
        
        EmergencyContactResponse newContact = emergencyContactService.addEmergencyContact(userDetails.getId(), request);
        
        String message = messageSource.getMessage("emergency.contact.add.success", null, locale);
        ApiResponse<EmergencyContactResponse> response = new ApiResponse<>(message, newContact, LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update Emergency Contact", description = "Updates an existing emergency contact for the authenticated user.")
    @PutMapping("/{contactId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmergencyContactResponse>> updateEmergencyContact(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contactId,
            @RequestBody @Valid UpdateEmergencyContactRequest request,
            Locale locale) {
        
        EmergencyContactResponse updatedContact = emergencyContactService.updateEmergencyContact(userDetails.getId(), contactId, request);
        
        String message = messageSource.getMessage("emergency.contact.update.success", null, locale);

        ApiResponse<EmergencyContactResponse> response = new ApiResponse<>(message, updatedContact, LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete Emergency Contact", description = "Deletes an emergency contact by ID for the authenticated user.")
    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmergencyContact(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contactId,
            Locale locale) {
        
        emergencyContactService.deleteEmergencyContact(userDetails.getId(), contactId);
        
        String message = messageSource.getMessage("emergency.contact.delete.success", null, locale);

        ApiResponse<Void> response = new ApiResponse<>(message, null, LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}
