package com.btg.core.application.port.in.group;

public interface GetGroupUseCase {
    GroupDetailResult getGroup(Long groupId);

    record GroupDetailResult(
        Long id,
        String name,
        String description,
        Integer memberCount,
        Integer maxMembers,
        UserInfo createdBy,
        Long createdAt,
        String myRole,  // "ADMIN", "MEMBER", "NONE"
        Integer taskCount
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
