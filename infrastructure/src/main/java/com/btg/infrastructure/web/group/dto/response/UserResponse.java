package com.btg.infrastructure.web.group.dto.response;

public record UserResponse(
    Long id,
    String email,
    String name
) {}
