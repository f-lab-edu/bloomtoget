package com.btg.infrastructure.web.task.dto.response;

public record UserResponse(
    Long id,
    String email,
    String name
) {}
