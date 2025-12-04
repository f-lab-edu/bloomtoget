package com.btg.core.application.port.out.group;

public interface DeleteGroupMemberPort {
    void deleteByGroupId(Long groupId);
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
