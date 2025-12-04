package com.btg.infrastructure.web.group.dto.response;

public record GroupMemberResponse(
    Long id,
    UserResponse user,
    String role,  // "ADMIN" or "MEMBER"
    Long joinedAt
) {}
