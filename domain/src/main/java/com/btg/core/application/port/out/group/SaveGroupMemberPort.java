package com.btg.core.application.port.out.group;

public interface SaveGroupMemberPort {
    GroupMember save(Long groupId, Long userId, String role);

    record GroupMember(
        Long id,
        Long groupId,
        Long userId,
        String role,
        Long joinedAt
    ) {}
}
