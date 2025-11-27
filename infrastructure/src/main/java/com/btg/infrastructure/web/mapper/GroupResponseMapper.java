package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.group.CreateGroupUseCase;
import com.btg.core.application.port.in.group.GetGroupUseCase;
import com.btg.core.application.port.in.group.ListGroupsUseCase;
import com.btg.core.application.port.in.group.UpdateGroupUseCase;
import com.btg.infrastructure.web.group.dto.response.GroupDetailResponse;
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

    /**
     * CreateGroupUseCase.GroupResult → GroupResponse
     * createGroup 메서드에서 사용
     */
    GroupResponse toResponse(CreateGroupUseCase.GroupResult result);

    /**
     * UpdateGroupUseCase.GroupResult → GroupResponse
     * updateGroup 메서드에서 사용
     */
    GroupResponse toResponse(UpdateGroupUseCase.GroupResult result);

    /**
     * GetGroupUseCase.GroupDetailResult → GroupDetailResponse
     * getGroup 메서드에서 사용
     */
    GroupDetailResponse toDetailResponse(GetGroupUseCase.GroupDetailResult result);

    /**
     * ListGroupsUseCase.GroupSummary → GroupResponse
     * List 변환의 기본 단위
     */
    GroupResponse toResponse(ListGroupsUseCase.GroupSummary summary);

    /**
     * List<GroupSummary> → List<GroupResponse>
     * listGroups의 content 변환용
     */
    List<GroupResponse> toResponseList(List<ListGroupsUseCase.GroupSummary> summaries);

    /**
     * ListGroupsUseCase.PagedGroupResult → PagedGroupResponse
     * listGroups 메서드에서 사용 (content List 자동 변환 포함)
     */
    PagedGroupResponse toPagedResponse(ListGroupsUseCase.PagedGroupResult result);
}
