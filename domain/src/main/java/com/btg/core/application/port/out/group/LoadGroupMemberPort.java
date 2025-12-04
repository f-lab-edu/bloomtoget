package com.btg.core.application.port.out.group;

import java.util.Optional;

public interface LoadGroupMemberPort {
    Optional<GroupMember> loadByGroupIdAndUserId(Long groupId, Long userId);
    java.util.List<GroupMember> loadByGroupId(Long groupId);
    Optional<GroupMember> loadOldestMemberByGroupId(Long groupId, Long excludeUserId);
    int countByGroupId(Long groupId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    record GroupMember(
        Long id,
        Long groupId,
        Long userId,
        String role,
        Long joinedAt
    ) {}
}
