package com.ewsaly.ewsaly.service.qrCode;

import com.ewsaly.ewsaly.dto.qrcode.CreateQrCodeRequest;
import com.ewsaly.ewsaly.dto.qrcode.PublicQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.UserQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.UpdateQrCodeRequest;
import com.ewsaly.ewsaly.exceptions.MaxQrCodeLimitExceededException;
import com.ewsaly.ewsaly.exceptions.QrCodeNotFoundException;
import com.ewsaly.ewsaly.mapper.QrCodeMapper;
import com.ewsaly.ewsaly.models.QrCode;
import com.ewsaly.ewsaly.models.QrCodeDesign;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.QrCodeRepository;
import com.ewsaly.ewsaly.service.user.UserService;
import com.ewsaly.ewsaly.utils.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrCodeServiceImpl implements QrCodeService {
    private static final int MAX_FREE_QR_LIMIT = 5;

    private final QrCodeRepository qrCodeRepository;
    private final UserService userService;
    private final QrCodeMapper qrCodeMapper;

    @Override
    @Transactional
    public UserQrCodeResponse createQrCode(Long userId, CreateQrCodeRequest request) {
        User user = userService.getUserById(userId);

        if (qrCodeRepository.countByUserId(userId) >= MAX_FREE_QR_LIMIT) {
            throw new MaxQrCodeLimitExceededException("error.qrcode.limit_exceeded");
        }

        String publicToken;
        do {
            publicToken = TokenGenerator.generateToken();
        } while (qrCodeRepository.existsByPublicToken(publicToken));

        QrCode.QrCodeBuilder builder = QrCode.builder()
                .user(user)
                .publicToken(publicToken)
                .itemType(request.getItemType())
                .itemName(request.getItemName())
                .isActive(true);

        if (request.getDesign() != null) {
            builder.design(qrCodeMapper.toDesignEntity(request.getDesign()));
        }

        QrCode qrCode = builder.build();

        QrCode savedQrCode = qrCodeRepository.save(qrCode);

        return qrCodeMapper.toUserResponse(savedQrCode);
    }

    @Override
    public UserQrCodeResponse getQrCodeById(Long userId, Long qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndUserId(qrCodeId, userId)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        return qrCodeMapper.toUserResponse(qrCode);
    }

    @Override
    public PublicQrCodeResponse getQrCodeByPublicToken(String publicToken) {
        QrCode qrCode = qrCodeRepository.findByPublicTokenAndIsActiveTrue(publicToken)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        return qrCodeMapper.toPublicResponse(qrCode);
    }

    @Override
    public List<UserQrCodeResponse> getUserQrCodes(Long userId, Boolean active) {
        userService.getUserById(userId);
        if (Boolean.TRUE.equals(active)) {
            return qrCodeMapper.toUserResponseList(qrCodeRepository.findAllByUserIdAndIsActiveTrue(userId));
        }
        return qrCodeMapper.toUserResponseList(qrCodeRepository.findAllByUserId(userId));
    }

    @Override
    @Transactional
    public UserQrCodeResponse updateQrCode(Long userId, Long qrCodeId, UpdateQrCodeRequest request) {
        QrCode qrCode = qrCodeRepository.findByIdAndUserId(qrCodeId, userId)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        if (request.getItemName() != null) {
            qrCode.setItemName(request.getItemName());
        }

        if (request.getIsActive() != null) {
            qrCode.setIsActive(request.getIsActive());
        }

        if (request.getDesign() != null) {
            if (qrCode.getDesign() == null) {
                qrCode.setDesign(new QrCodeDesign());
            }
            qrCodeMapper.updateDesignFromDto(request.getDesign(), qrCode.getDesign());
        }

        QrCode updatedQrCode = qrCodeRepository.save(qrCode);

        return qrCodeMapper.toUserResponse(updatedQrCode);
    }

    @Override
    @Transactional
    public UserQrCodeResponse toggleQrCodeActiveStatus(Long userId, Long qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndUserId(qrCodeId, userId)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        qrCode.setIsActive(!qrCode.getIsActive());

        QrCode updatedQrCode = qrCodeRepository.save(qrCode);

        return qrCodeMapper.toUserResponse(updatedQrCode);
    }

    @Override
    @Transactional
    public void deleteQrCode(Long userId, Long qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndUserId(qrCodeId, userId)
                .orElseThrow(() -> new QrCodeNotFoundException("error.qrcode.not_found"));

        qrCodeRepository.delete(qrCode);
    }
}
