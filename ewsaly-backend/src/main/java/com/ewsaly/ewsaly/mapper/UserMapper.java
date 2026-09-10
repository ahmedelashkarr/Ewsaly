package com.ewsaly.ewsaly.mapper;

import com.ewsaly.ewsaly.dto.auth.SignUpRequest;
import com.ewsaly.ewsaly.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(SignUpRequest request);

    @Mapping(target = "passwordHash", ignore = true)
    void updateEntityFromRequest(SignUpRequest request, @MappingTarget User user);
}
