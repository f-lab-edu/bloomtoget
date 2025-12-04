package com.btg.core.application.port.out.group;

public interface UpdateGroupMemberPort {
    void updateRole(Long groupMemberId, String newRole);
}
