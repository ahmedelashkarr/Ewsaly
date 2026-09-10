package com.ewsaly.ewsaly.service.qrCode;

import com.ewsaly.ewsaly.dto.qrcode.*;
import com.ewsaly.ewsaly.enums.ItemType;
import com.ewsaly.ewsaly.exceptions.MaxQrCodeLimitExceededException;
import com.ewsaly.ewsaly.exceptions.QrCodeNotFoundException;
import com.ewsaly.ewsaly.mapper.QrCodeMapper;
import com.ewsaly.ewsaly.models.QrCode;
import com.ewsaly.ewsaly.models.QrCodeDesign;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.QrCodeRepository;
import com.ewsaly.ewsaly.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceImplTest {

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private UserService userService;

    @Mock
    private QrCodeMapper qrCodeMapper;

    @InjectMocks
    private QrCodeServiceImpl qrCodeService;

    private User user;
    private QrCode qrCode;
    private QrCodeDesign design;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .phoneNumber("+201012345678")
                .build();

        design = QrCodeDesign.builder()
                .logoBrand("BMW")
                .patternColor("#000000")
                .build();

        qrCode = QrCode.builder()
                .id(100L)
                .user(user)
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .isActive(true)
                .design(design)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new QR Code")
    void createQrCode_Success() {
        CreateQrCodeRequest request = CreateQrCodeRequest.builder()
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .design(QrCodeDesignDto.builder().logoBrand("BMW").build())
                .build();

        when(userService.getUserById(1L)).thenReturn(user);
        when(qrCodeRepository.countByUserId(1L)).thenReturn(2L);
        when(qrCodeRepository.existsByPublicToken(anyString())).thenReturn(false);
        when(qrCodeMapper.toDesignEntity(any())).thenReturn(design);
        when(qrCodeRepository.save(any(QrCode.class))).thenReturn(qrCode);
        when(qrCodeMapper.toUserResponse(any(QrCode.class))).thenReturn(UserQrCodeResponse.builder()
                .id(100L)
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .isActive(true)
                .build());

        UserQrCodeResponse response = qrCodeService.createQrCode(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("ABC123XYZ4", response.getPublicToken());
        assertEquals(ItemType.CAR, response.getItemType());
        assertEquals("My Red BMW", response.getItemName());
        verify(qrCodeRepository, times(1)).save(any(QrCode.class));
    }

    @Test
    @DisplayName("Should throw MaxQrCodeLimitExceededException when user reaches free limit of 5")
    void createQrCode_MaxLimitExceeded_ThrowsException() {
        CreateQrCodeRequest request = CreateQrCodeRequest.builder()
                .itemType(ItemType.CAR)
                .itemName("My Red BMW")
                .build();

        when(userService.getUserById(1L)).thenReturn(user);
        when(qrCodeRepository.countByUserId(1L)).thenReturn(5L);

        assertThrows(MaxQrCodeLimitExceededException.class, () ->
                qrCodeService.createQrCode(1L, request)
        );
        verify(qrCodeRepository, never()).save(any(QrCode.class));
    }

    @Test
    @DisplayName("Should retrieve QR Code by ID for owner")
    void getQrCodeById_Success() {
        when(qrCodeRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(qrCode));
        when(qrCodeMapper.toUserResponse(qrCode)).thenReturn(UserQrCodeResponse.builder()
                .id(100L)
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .isActive(true)
                .build());

        UserQrCodeResponse response = qrCodeService.getQrCodeById(1L, 100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    @DisplayName("Should throw QrCodeNotFoundException when QR Code ID is not found for user")
    void getQrCodeById_NotFound_ThrowsException() {
        when(qrCodeRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        assertThrows(QrCodeNotFoundException.class, () ->
                qrCodeService.getQrCodeById(1L, 100L)
        );
    }

    @Test
    @DisplayName("Should retrieve Public QR Code by active public token")
    void getQrCodeByPublicToken_Success() {
        when(qrCodeRepository.findByPublicTokenAndIsActiveTrue("ABC123XYZ4")).thenReturn(Optional.of(qrCode));
        when(qrCodeMapper.toPublicResponse(qrCode)).thenReturn(PublicQrCodeResponse.builder()
                .publicToken("ABC123XYZ4")
                .itemType(ItemType.CAR)
                .contactName("Test User")
                .build());

        PublicQrCodeResponse response = qrCodeService.getQrCodeByPublicToken("ABC123XYZ4");

        assertNotNull(response);
        assertEquals("ABC123XYZ4", response.getPublicToken());
        assertEquals(ItemType.CAR, response.getItemType());
        assertEquals("Test User", response.getContactName());
    }

    @Test
    @DisplayName("Should throw QrCodeNotFoundException when public token does not exist or is inactive")
    void getQrCodeByPublicToken_NotFound_ThrowsException() {
        when(qrCodeRepository.findByPublicTokenAndIsActiveTrue("INVALID_TOKEN")).thenReturn(Optional.empty());

        assertThrows(QrCodeNotFoundException.class, () ->
                qrCodeService.getQrCodeByPublicToken("INVALID_TOKEN")
        );
    }

    @Test
    @DisplayName("Should update QR Code active state and design")
    void updateQrCode_Success() {
        UpdateQrCodeRequest request = UpdateQrCodeRequest.builder()
                .itemName("Updated BMW Name")
                .isActive(false)
                .design(QrCodeDesignDto.builder().patternColor("#FFFFFF").build())
                .build();

        when(qrCodeRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(qrCode));
        when(qrCodeRepository.save(qrCode)).thenReturn(qrCode);
        when(qrCodeMapper.toUserResponse(qrCode)).thenReturn(UserQrCodeResponse.builder()
                .id(100L)
                .isActive(false)
                .build());

        UserQrCodeResponse response = qrCodeService.updateQrCode(1L, 100L, request);

        assertNotNull(response);
        assertFalse(qrCode.getIsActive());
        verify(qrCodeRepository, times(1)).save(qrCode);
    }

    @Test
    @DisplayName("Should toggle active status of QR Code")
    void toggleQrCodeActiveStatus_Success() {
        qrCode.setIsActive(true);
        when(qrCodeRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(qrCode));
        when(qrCodeRepository.save(qrCode)).thenReturn(qrCode);
        when(qrCodeMapper.toUserResponse(qrCode)).thenReturn(UserQrCodeResponse.builder()
                .id(100L)
                .isActive(false)
                .build());

        UserQrCodeResponse response = qrCodeService.toggleQrCodeActiveStatus(1L, 100L);

        assertNotNull(response);
        assertFalse(qrCode.getIsActive());
    }

    @Test
    @DisplayName("Should delete QR Code successfully")
    void deleteQrCode_Success() {
        when(qrCodeRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(qrCode));

        qrCodeService.deleteQrCode(1L, 100L);

        verify(qrCodeRepository, times(1)).delete(qrCode);
    }
}
