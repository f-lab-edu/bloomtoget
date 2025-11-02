package com.btg.infrastructure.web.user.dto.response;

public record UserResponse(
    Long id,
    String email,
    String name,
    String createdAt
) {}
