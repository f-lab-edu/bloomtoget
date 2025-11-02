package com.btg.infrastructure.web.group.dto.response;

import java.util.List;

public record PagedGroupResponse(
    List<GroupResponse> content,
    Integer totalElements,
    Integer totalPages,
    Integer page,
    Integer size
) {}
