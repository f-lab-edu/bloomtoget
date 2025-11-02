package com.btg.infrastructure.web.auth.dto.response;

import com.btg.infrastructure.web.user.dto.response.UserResponse;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    UserResponse user
) {}
