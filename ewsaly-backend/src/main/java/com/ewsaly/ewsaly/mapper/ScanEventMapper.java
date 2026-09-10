package com.ewsaly.ewsaly.mapper;

import com.ewsaly.ewsaly.dto.scanEvent.ScanEventResponse;
import com.ewsaly.ewsaly.models.ScanEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScanEventMapper {

    @Mapping(source = "qrCode.id", target = "qrCodeId")
    ScanEventResponse toResponse(ScanEvent scanEvent);

    List<ScanEventResponse> toResponseList(List<ScanEvent> scanEvents);
}
