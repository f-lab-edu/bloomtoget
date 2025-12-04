package com.btg.core.application.port.in.group;

public interface LeaveGroupUseCase {
    void leaveGroup(LeaveGroupCommand command);

    record LeaveGroupCommand(
        Long groupId,
        Long userId
    ) {
        public LeaveGroupCommand {
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }
}
