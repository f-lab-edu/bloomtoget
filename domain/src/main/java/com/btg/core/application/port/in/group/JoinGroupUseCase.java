package com.btg.core.application.port.in.group;

public interface JoinGroupUseCase {
    GroupMemberResult joinGroup(JoinGroupCommand command);

    record JoinGroupCommand(
        Long groupId,
        Long userId
    ) {
        public JoinGroupCommand {
            // Self-validation
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // TODO: Implement business logic in Service layer
            // - Check if user is already a member
            // - Check if group is full (maxMembers)
            // - Assign MEMBER role (not ADMIN)
        }
    }

    record GroupMemberResult(
        Long id,
        UserInfo user,
        String role,  // "ADMIN" or "MEMBER"
        String joinedAt
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
