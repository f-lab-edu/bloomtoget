package com.btg.core.application.port.in.group;

import java.util.List;

public interface ListGroupMembersUseCase {
    GroupMemberListResult listGroupMembers(ListGroupMembersQuery query);

    record ListGroupMembersQuery(
        Long groupId
    ) {
        public ListGroupMembersQuery {
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
        }
    }

    record GroupMemberListResult(
        List<GroupMemberInfo> members,
        Integer totalCount
    ) {}

    record GroupMemberInfo(
        Long id,
        UserInfo user,
        String role,
        Long joinedAt
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
