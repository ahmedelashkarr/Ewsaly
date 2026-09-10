package com.ewsaly.ewsaly.mapper;

import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;
import com.ewsaly.ewsaly.models.EmergencyContact;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmergencyContactMapper {

    EmergencyContactResponse toResponse(EmergencyContact contact);

    List<EmergencyContactResponse> toResponseList(List<EmergencyContact> contacts);

    EmergencyContact toEntity(CreateEmergencyContactRequest request);

    void updateEntityFromRequest(UpdateEmergencyContactRequest request, @MappingTarget EmergencyContact contact);
}
