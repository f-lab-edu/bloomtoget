package com.btg.infrastructure.web.task.dto.response;

import java.util.List;

public record TaskMemberListResponse(
    List<TaskMemberResponse> members,
    Integer totalCount,
    Double averageCompletionRate
) {}
