package com.btg.infrastructure.web.group.dto.response;

import java.util.List;

public record GroupMemberListResponse(
    List<GroupMemberResponse> members,
    Integer totalCount
) {}
