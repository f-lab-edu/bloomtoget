package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.task.*;
import com.btg.infrastructure.web.task.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaskResponseMapper {

    // CreateTaskUseCase 변환
    TaskResponse toResponse(CreateTaskUseCase.TaskResult result);
    UserResponse toUserResponse(CreateTaskUseCase.UserInfo userInfo);

    // UpdateTaskUseCase 변환
    TaskResponse toResponse(UpdateTaskUseCase.TaskResult result);
    UserResponse toUserResponse(UpdateTaskUseCase.UserInfo userInfo);

    // UpdateTaskStatusUseCase 변환
    TaskResponse toResponse(UpdateTaskStatusUseCase.TaskResult result);
    UserResponse toUserResponse(UpdateTaskStatusUseCase.UserInfo userInfo);

    // GetTaskUseCase 변환
    TaskDetailResponse toDetailResponse(GetTaskUseCase.TaskDetailResult result);
    UserResponse toUserResponse(GetTaskUseCase.UserInfo userInfo);

    // ListTasksUseCase 변환
    TaskResponse toResponse(ListTasksUseCase.TaskSummary summary);
    UserResponse toUserResponse(ListTasksUseCase.UserInfo userInfo);
    List<TaskResponse> toResponseList(List<ListTasksUseCase.TaskSummary> summaries);
    PagedTaskResponse toPagedResponse(ListTasksUseCase.PagedTaskResult result);

    // JoinTaskUseCase 변환
    TaskMemberResponse toMemberResponse(JoinTaskUseCase.TaskMemberResult result);
    UserResponse toUserResponse(JoinTaskUseCase.UserInfo userInfo);

    // ListTaskMembersUseCase 변환
    TaskMemberResponse toMemberResponse(ListTaskMembersUseCase.TaskMemberInfo memberInfo);
    UserResponse toUserResponse(ListTaskMembersUseCase.UserInfo userInfo);
    List<TaskMemberResponse> toMemberResponseList(List<ListTaskMembersUseCase.TaskMemberInfo> members);
    TaskMemberListResponse toMemberListResponse(ListTaskMembersUseCase.TaskMemberListResult result);

    // GetTaskProgressUseCase 변환
    TaskProgressResponse toProgressResponse(GetTaskProgressUseCase.TaskProgressResult result);
}
