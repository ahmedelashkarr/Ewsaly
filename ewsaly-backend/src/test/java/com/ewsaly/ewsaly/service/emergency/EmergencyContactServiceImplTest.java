package com.ewsaly.ewsaly.service.emergency;

import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;
import com.ewsaly.ewsaly.exceptions.EmergencyContactNotFoundException;
import com.ewsaly.ewsaly.models.EmergencyContact;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.EmergencyContactRepository;
import com.ewsaly.ewsaly.mapper.EmergencyContactMapper;
import com.ewsaly.ewsaly.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergencyContactServiceImplTest {

    @Mock
    private EmergencyContactRepository emergencyContactRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmergencyContactMapper emergencyContactMapper;

    @InjectMocks
    private EmergencyContactServiceImpl emergencyContactService;

    private User user;
    private EmergencyContact contact;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .phoneNumber("+201012345678")
                .emergencyContacts(new HashSet<>())
                .build();

        contact = EmergencyContact.builder()
                .id(10L)
                .name("Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully add emergency contact")
    void addEmergencyContact_Success() {
        CreateEmergencyContactRequest request = CreateEmergencyContactRequest.builder()
                .name("Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();

        when(userService.getUserById(1L)).thenReturn(user);
        when(emergencyContactMapper.toEntity(any(CreateEmergencyContactRequest.class))).thenReturn(contact);
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenReturn(contact);
        when(emergencyContactMapper.toResponse(any(EmergencyContact.class))).thenReturn(EmergencyContactResponse.builder()
                .id(10L).name("Father").phoneNumber("+201098765432").relationship("Father").build());

        EmergencyContactResponse response = emergencyContactService.addEmergencyContact(1L, request);

        assertNotNull(response);
        assertEquals("Father", response.getName());
        assertEquals("+201098765432", response.getPhoneNumber());
    }

    @Test
    @DisplayName("Should successfully update emergency contact")
    void updateEmergencyContact_Success() {
        UpdateEmergencyContactRequest request = UpdateEmergencyContactRequest.builder()
                .name("Updated Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();

        when(emergencyContactRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(contact));
        doNothing().when(emergencyContactMapper).updateEntityFromRequest(any(UpdateEmergencyContactRequest.class), any(EmergencyContact.class));
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenReturn(contact);
        when(emergencyContactMapper.toResponse(any(EmergencyContact.class))).thenReturn(EmergencyContactResponse.builder()
                .id(10L).name("Updated Father").phoneNumber("+201098765432").relationship("Father").build());

        EmergencyContactResponse response = emergencyContactService.updateEmergencyContact(1L, 10L, request);

        assertNotNull(response);
        verify(emergencyContactRepository, times(1)).save(contact);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing contact")
    void updateEmergencyContact_NotFound_ThrowsException() {
        UpdateEmergencyContactRequest request = UpdateEmergencyContactRequest.builder()
                .name("Updated Father")
                .phoneNumber("+201098765432")
                .relationship("Father")
                .build();

        when(emergencyContactRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThrows(EmergencyContactNotFoundException.class, () ->
                emergencyContactService.updateEmergencyContact(1L, 10L, request)
        );
    }

    @Test
    @DisplayName("Should delete emergency contact")
    void deleteEmergencyContact_Success() {
        user.getEmergencyContacts().add(contact);

        when(userService.getUserById(1L)).thenReturn(user);
        when(emergencyContactRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(contact));

        emergencyContactService.deleteEmergencyContact(1L, 10L);

        assertFalse(user.getEmergencyContacts().contains(contact));
        verify(emergencyContactRepository, times(1)).delete(contact);
    }

    @Test
    @DisplayName("Should return list of emergency contacts for user")
    void getUserEmergencyContacts_Success() {
        when(emergencyContactRepository.findAllByUserId(1L)).thenReturn(List.of(contact));
        when(emergencyContactMapper.toResponseList(anyList())).thenReturn(List.of(
                EmergencyContactResponse.builder().id(10L).name("Father").phoneNumber("+201098765432").relationship("Father").build()
        ));

        List<EmergencyContactResponse> list = emergencyContactService.getUserEmergencyContacts(1L);

        assertEquals(1, list.size());
        assertEquals("Father", list.get(0).getName());
    }
}
