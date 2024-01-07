package com.barisd.mapper;

import com.barisd.dto.request.RegisterRequestDto;
import com.barisd.dto.request.UserProfileSaveRequestDto;
import com.barisd.repository.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthMapper {
    IAuthMapper INSTANCE= Mappers.getMapper(IAuthMapper.class);
    Auth registerRequestDtoToAuth(RegisterRequestDto dto);
    @Mapping(source = "id", target = "authid")
    UserProfileSaveRequestDto fromAuth(Auth auth);
}
