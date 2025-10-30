package com.btg.infrastructure.web.task.dto.response;

import java.util.List;

public record PagedTaskResponse(
    List<TaskResponse> content,
    Integer totalElements,
    Integer totalPages,
    Integer page,
    Integer size
) {}
