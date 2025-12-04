package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.group.CreateGroupUseCase;
import com.btg.core.application.port.in.group.GetGroupUseCase;
import com.btg.core.application.port.in.group.ListGroupMembersUseCase;
import com.btg.core.application.port.in.group.ListGroupsUseCase;
import com.btg.core.application.port.in.group.SearchGroupsUseCase;
import com.btg.core.application.port.in.group.UpdateGroupUseCase;
import com.btg.infrastructure.web.group.dto.response.GroupDetailResponse;
import com.btg.infrastructure.web.group.dto.response.GroupMemberListResponse;
import com.btg.infrastructure.web.group.dto.response.GroupMemberResponse;
import com.btg.infrastructure.web.group.dto.response.GroupResponse;
import com.btg.infrastructure.web.group.dto.response.PagedGroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Group 관련 UseCase Result → Response DTO 변환 Mapper
 * MapStruct가 중첩 객체(UserInfo → UserResponse)를 자동으로 처리합니다.
 */
@Mapper(
    componentModel = "spring",
    uses = {UserResponseMapper.class},  // 중첩 변환용 UserResponseMapper 연결
    unmappedTargetPolicy = ReportingPolicy.ERROR  // 매핑 누락 시 컴파일 에러 발생
)
public interface GroupResponseMapper {


    GroupResponse toResponse(CreateGroupUseCase.GroupResult result);


    GroupResponse toResponse(UpdateGroupUseCase.GroupResult result);


    GroupDetailResponse toDetailResponse(GetGroupUseCase.GroupDetailResult result);


    GroupResponse toResponse(ListGroupsUseCase.GroupSummary summary);


    List<GroupResponse> toResponseList(List<ListGroupsUseCase.GroupSummary> summaries);


    PagedGroupResponse toPagedResponse(ListGroupsUseCase.PagedGroupResult result);


    GroupResponse toResponse(SearchGroupsUseCase.GroupSummary summary);


    List<GroupResponse> toSearchResponseList(List<SearchGroupsUseCase.GroupSummary> summaries);

    PagedGroupResponse toPagedResponse(SearchGroupsUseCase.PagedGroupResult result);


    GroupMemberResponse toMemberResponse(ListGroupMembersUseCase.GroupMemberInfo memberInfo);

    List<GroupMemberResponse> toMemberResponseList(List<ListGroupMembersUseCase.GroupMemberInfo> members);

    GroupMemberListResponse toMemberListResponse(ListGroupMembersUseCase.GroupMemberListResult result);
}
