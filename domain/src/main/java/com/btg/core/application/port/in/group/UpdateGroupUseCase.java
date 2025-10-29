package com.btg.core.application.port.in.group;

public interface UpdateGroupUseCase {
    GroupResult updateGroup(UpdateGroupCommand command);

    record UpdateGroupCommand(
        Long groupId,
        Long userId,
        String name,
        String description,
        Integer maxMembers
    ) {
        public UpdateGroupCommand {
            // Self-validation
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // Optional: name
            if (name != null && !name.isBlank()) {
                if (name.length() < 2 || name.length() > 100) {
                    throw new IllegalArgumentException("Name must be between 2 and 100 characters");
                }
            }
            // Optional: description
            if (description != null && !description.isBlank()) {
                if (description.length() > 500) {
                    throw new IllegalArgumentException("Description must not exceed 500 characters");
                }
            }
            // Optional: maxMembers
            if (maxMembers != null) {
                if (maxMembers < 2 || maxMembers > 100) {
                    throw new IllegalArgumentException("Max members must be between 2 and 100");
                }
            }
        }
    }

    record GroupResult(
        Long id,
        String name,
        String description,
        Integer memberCount,
        Integer maxMembers,
        UserInfo createdBy,
        String createdAt
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
