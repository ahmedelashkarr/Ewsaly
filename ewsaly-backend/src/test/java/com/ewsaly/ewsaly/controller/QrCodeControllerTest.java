package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.qrcode.*;
import com.ewsaly.ewsaly.enums.ItemType;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import com.ewsaly.ewsaly.service.qrCode.QrCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeControllerTest {

    @Mock
    private QrCodeService qrCodeService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private QrCodeController qrCodeController;

    private CustomUserDetails userDetails;
    private UserQrCodeResponse userQrResponse;
    private PublicQrCodeResponse publicQrResponse;
    private Locale locale;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1L);
        userDetails = new CustomUserDetails(mockUser);
        locale = Locale.getDefault();

        userQrResponse = UserQrCodeResponse.builder()
                .id(100L)
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .isActive(true)
                .build();

        publicQrResponse = PublicQrCodeResponse.builder()
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .build();
    }

    @Test
    @DisplayName("Should create QR Code and return 201 CREATED")
    void createQrCode_Success() {
        CreateQrCodeRequest request = CreateQrCodeRequest.builder()
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .build();

        when(qrCodeService.createQrCode(eq(1L), any(CreateQrCodeRequest.class))).thenReturn(userQrResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Created successfully");

        ResponseEntity<ApiResponse<UserQrCodeResponse>> response =
                qrCodeController.createQrCode(userDetails, request, locale);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created successfully", response.getBody().message());
        assertEquals(100L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should retrieve all QR Codes for authenticated user")
    void getUserQrCodes_Success() {
        when(qrCodeService.getUserQrCodes(1L, null)).thenReturn(List.of(userQrResponse));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Fetched successfully");

        ResponseEntity<ApiResponse<List<UserQrCodeResponse>>> response =
                qrCodeController.getUserQrCodes(userDetails, null, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
    }

    @Test
    @DisplayName("Should retrieve active QR Codes for authenticated user")
    void getUserActiveQrCodes_Success() {
        when(qrCodeService.getUserQrCodes(1L, true)).thenReturn(List.of(userQrResponse));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Fetched successfully");

        ResponseEntity<ApiResponse<List<UserQrCodeResponse>>> response =
                qrCodeController.getUserQrCodes(userDetails, true, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
    }

    @Test
    @DisplayName("Should retrieve specific QR Code by ID")
    void getQrCodeById_Success() {
        when(qrCodeService.getQrCodeById(1L, 100L)).thenReturn(userQrResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Fetched successfully");

        ResponseEntity<ApiResponse<UserQrCodeResponse>> response =
                qrCodeController.getQrCodeById(userDetails, 100L, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should retrieve Public QR Code by token")
    void getQrCodeByPublicToken_Success() {
        when(qrCodeService.getQrCodeByPublicToken("ABC123XYZ4")).thenReturn(publicQrResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Public QR fetched");

        ResponseEntity<ApiResponse<PublicQrCodeResponse>> response =
                qrCodeController.getQrCodeByPublicToken("ABC123XYZ4", locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABC123XYZ4", response.getBody().data().getPublicToken());
    }

    @Test
    @DisplayName("Should update QR Code")
    void updateQrCode_Success() {
        UpdateQrCodeRequest request = UpdateQrCodeRequest.builder()
                .isActive(false)
                .build();

        when(qrCodeService.updateQrCode(eq(1L), eq(100L), any(UpdateQrCodeRequest.class))).thenReturn(userQrResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Updated successfully");

        ResponseEntity<ApiResponse<UserQrCodeResponse>> response =
                qrCodeController.updateQrCode(userDetails, 100L, request, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should toggle QR Code active status")
    void toggleQrCodeActiveStatus_Success() {
        when(qrCodeService.toggleQrCodeActiveStatus(1L, 100L)).thenReturn(userQrResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Toggled successfully");

        ResponseEntity<ApiResponse<UserQrCodeResponse>> response =
                qrCodeController.toggleQrCodeActiveStatus(userDetails, 100L, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should delete QR Code")
    void deleteQrCode_Success() {
        doNothing().when(qrCodeService).deleteQrCode(1L, 100L);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Deleted successfully");

        ResponseEntity<ApiResponse<Void>> response =
                qrCodeController.deleteQrCode(userDetails, 100L, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().data());

        verify(qrCodeService, times(1)).deleteQrCode(1L, 100L);
    }
}
