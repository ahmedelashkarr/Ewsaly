package com.ewsaly.ewsaly.service.qrCode;

import com.ewsaly.ewsaly.dto.qrcode.CreateQrCodeRequest;
import com.ewsaly.ewsaly.dto.qrcode.PublicQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.UserQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.UpdateQrCodeRequest;

import java.util.List;

public interface QrCodeService {

    UserQrCodeResponse createQrCode(Long userId, CreateQrCodeRequest request);

    UserQrCodeResponse getQrCodeById(Long userId, Long qrCodeId);

    PublicQrCodeResponse getQrCodeByPublicToken(String publicToken);

    List<UserQrCodeResponse> getUserQrCodes(Long userId, Boolean active);

    UserQrCodeResponse updateQrCode(Long userId, Long qrCodeId, UpdateQrCodeRequest request);

    UserQrCodeResponse toggleQrCodeActiveStatus(Long userId, Long qrCodeId);

    void deleteQrCode(Long userId, Long qrCodeId);
}
