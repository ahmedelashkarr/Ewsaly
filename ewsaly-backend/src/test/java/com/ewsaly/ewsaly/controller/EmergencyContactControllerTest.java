package com.ewsaly.ewsaly.controller;

import com.ewsaly.ewsaly.dto.ApiResponse;
import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import com.ewsaly.ewsaly.service.emergency.EmergencyContactService;
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
class EmergencyContactControllerTest {

    @Mock
    private EmergencyContactService emergencyContactService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmergencyContactController emergencyContactController;

    private CustomUserDetails userDetails;
    private EmergencyContactResponse contactResponse;
    private Locale locale;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1L);
        userDetails = new CustomUserDetails(mockUser);
        locale = Locale.getDefault();

        contactResponse = EmergencyContactResponse.builder()
                .id(10L)
                .name("Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();
    }

    @Test
    @DisplayName("Should get all user emergency contacts")
    void getUserEmergencyContacts_Success() {
        when(emergencyContactService.getUserEmergencyContacts(1L)).thenReturn(List.of(contactResponse));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Success");

        ResponseEntity<ApiResponse<List<EmergencyContactResponse>>> response = 
                emergencyContactController.getUserEmergencyContacts(userDetails, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success", response.getBody().message());
        assertEquals(1, response.getBody().data().size());
        assertEquals(10L, response.getBody().data().get(0).getId());
    }

    @Test
    @DisplayName("Should add emergency contact")
    void addEmergencyContact_Success() {
        CreateEmergencyContactRequest request = CreateEmergencyContactRequest.builder()
                .name("Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();

        when(emergencyContactService.addEmergencyContact(eq(1L), any(CreateEmergencyContactRequest.class)))
                .thenReturn(contactResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Success");

        ResponseEntity<ApiResponse<EmergencyContactResponse>> response = 
                emergencyContactController.addEmergencyContact(userDetails, request, locale);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success", response.getBody().message());
        assertEquals(10L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should update emergency contact")
    void updateEmergencyContact_Success() {
        UpdateEmergencyContactRequest request = UpdateEmergencyContactRequest.builder()
                .name("Updated Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();

        when(emergencyContactService.updateEmergencyContact(eq(1L), eq(10L), any(UpdateEmergencyContactRequest.class)))
                .thenReturn(contactResponse);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Success");

        ResponseEntity<ApiResponse<EmergencyContactResponse>> response = 
                emergencyContactController.updateEmergencyContact(userDetails, 10L, request, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success", response.getBody().message());
        assertEquals(10L, response.getBody().data().getId());
    }

    @Test
    @DisplayName("Should delete emergency contact")
    void deleteEmergencyContact_Success() {
        doNothing().when(emergencyContactService).deleteEmergencyContact(1L, 10L);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Success");

        ResponseEntity<ApiResponse<Void>> response = 
                emergencyContactController.deleteEmergencyContact(userDetails, 10L, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success", response.getBody().message());
        assertNull(response.getBody().data());
        
        verify(emergencyContactService, times(1)).deleteEmergencyContact(1L, 10L);
    }
}
