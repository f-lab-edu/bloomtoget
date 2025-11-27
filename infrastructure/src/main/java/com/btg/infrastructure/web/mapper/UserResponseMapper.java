package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.group.*;
import com.btg.core.application.port.in.user.GetUserProfileUseCase;
import com.btg.core.application.port.in.user.UpdateUserProfileUseCase;
import com.btg.infrastructure.web.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserResponseMapper {

    // Group 도메인용 UserInfo 변환 (createdAt 없음)
    @Mapping(target = "createdAt", ignore = true)
    UserResponse toResponse(CreateGroupUseCase.UserInfo userInfo);

    @Mapping(target = "createdAt", ignore = true)
    UserResponse toResponse(GetGroupUseCase.UserInfo userInfo);

    @Mapping(target = "createdAt", ignore = true)
    UserResponse toResponse(ListGroupsUseCase.UserInfo userInfo);

    @Mapping(target = "createdAt", ignore = true)
    UserResponse toResponse(UpdateGroupUseCase.UserInfo userInfo);

    @Mapping(target = "createdAt", ignore = true)
    UserResponse toResponse(JoinGroupUseCase.UserInfo userInfo);

    // User 도메인용 UserProfileResult 변환
    UserResponse toResponse(GetUserProfileUseCase.UserProfileResult result);

    UserResponse toResponse(UpdateUserProfileUseCase.UserProfileResult result);
}
