package com.btg.infrastructure.web.group.dto.response;

public record GroupResponse(
    Long id,
    String name,
    String description,
    Integer memberCount,
    Integer maxMembers,
    UserResponse createdBy,
    String createdAt
) {}
