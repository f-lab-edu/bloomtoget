package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.auth.LoginUseCase;
import com.btg.core.application.port.in.auth.RefreshTokenUseCase;
import com.btg.core.application.port.in.auth.SignupUseCase;
import com.btg.infrastructure.web.auth.dto.response.LoginResponse;
import com.btg.infrastructure.web.auth.dto.response.TokenResponse;
import com.btg.infrastructure.web.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthResponseMapper {

    // SignupUseCase 변환
    UserResponse toUserResponse(SignupUseCase.UserResult result);

    // LoginUseCase 변환
    LoginResponse toLoginResponse(LoginUseCase.LoginResult result);
    UserResponse toUserResponse(LoginUseCase.LoginResult.UserInfo userInfo);

    // RefreshTokenUseCase 변환
    TokenResponse toTokenResponse(RefreshTokenUseCase.TokenResult result);
}
