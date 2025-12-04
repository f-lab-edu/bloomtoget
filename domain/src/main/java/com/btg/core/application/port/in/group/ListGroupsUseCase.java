package com.btg.core.application.port.in.group;

import java.util.List;

public interface ListGroupsUseCase {
    PagedGroupResult listGroups(ListGroupsQuery query);

    record ListGroupsQuery(
        Long userId,
        String type,  // "MY" or "ALL"
        Integer page,
        Integer size
    ) {
        public ListGroupsQuery {
            // Self-validation
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (type == null || type.isBlank()) {
                throw new IllegalArgumentException("Type is required");
            }
            if (!type.equals("MY") && !type.equals("ALL")) {
                throw new IllegalArgumentException("Type must be either 'MY' or 'ALL'");
            }
            // Optional: page and size (defaults will be handled in service)
            if (page != null && page < 0) {
                throw new IllegalArgumentException("Page must not be negative");
            }
            if (size != null && (size < 1 || size > 100)) {
                throw new IllegalArgumentException("Size must be between 1 and 100");
            }
        }
    }

    record PagedGroupResult(
        List<GroupSummary> content,
        Integer totalElements,
        Integer totalPages,
        Integer page,
        Integer size
    ) {}

    record GroupSummary(
        Long id,
        String name,
        String description,
        Integer memberCount,
        Integer maxMembers,
        UserInfo createdBy,
        Long createdAt
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
