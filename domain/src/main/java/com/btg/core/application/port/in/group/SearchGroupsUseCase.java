package com.btg.core.application.port.in.group;

import java.util.List;

public interface SearchGroupsUseCase {
    PagedGroupResult searchGroups(SearchGroupsQuery query);

    record SearchGroupsQuery(
        String keyword,
        Integer page,
        Integer size
    ) {
        public SearchGroupsQuery {
            if (keyword == null || keyword.isBlank()) {
                throw new IllegalArgumentException("Keyword is required");
            }
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
