package com.ewsaly.ewsaly.mapper;

import com.ewsaly.ewsaly.dto.qrcode.PublicQrCodeResponse;
import com.ewsaly.ewsaly.dto.qrcode.QrCodeDesignDto;
import com.ewsaly.ewsaly.dto.qrcode.UserQrCodeResponse;
import com.ewsaly.ewsaly.models.QrCode;
import com.ewsaly.ewsaly.models.QrCodeDesign;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QrCodeMapper {

    UserQrCodeResponse toUserResponse(QrCode qrCode);

    List<UserQrCodeResponse> toUserResponseList(List<QrCode> qrCodes);

    @Mapping(source = "user.fullName", target = "contactName")
    PublicQrCodeResponse toPublicResponse(QrCode qrCode);

    QrCodeDesign toDesignEntity(QrCodeDesignDto dto);

    QrCodeDesignDto toDesignDto(QrCodeDesign entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDesignFromDto(QrCodeDesignDto dto, @MappingTarget QrCodeDesign entity);
}
