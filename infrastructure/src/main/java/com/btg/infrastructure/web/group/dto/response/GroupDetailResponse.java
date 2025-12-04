package com.btg.infrastructure.web.group.dto.response;

public record GroupDetailResponse(
    Long id,
    String name,
    String description,
    Integer memberCount,
    Integer maxMembers,
    UserResponse createdBy,
    Long createdAt,
    String myRole,  // "ADMIN", "MEMBER", "NONE"
    Integer taskCount
) {}
