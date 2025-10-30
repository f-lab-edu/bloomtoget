package com.btg.core.application.port.in.group;

public interface DeleteGroupUseCase {
    void deleteGroup(DeleteGroupCommand command);

    record DeleteGroupCommand(
        Long groupId,
        Long userId
    ) {
        public DeleteGroupCommand {
            // Self-validation
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // TODO: Implement authorization check in Service layer
            // Only ADMIN can delete group
        }
    }
}
