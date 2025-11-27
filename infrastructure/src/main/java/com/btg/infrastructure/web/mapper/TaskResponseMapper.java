package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.task.CreateTaskUseCase;
import com.btg.core.application.port.in.task.GetTaskUseCase;
import com.btg.core.application.port.in.task.ListTasksUseCase;
import com.btg.core.application.port.in.task.UpdateTaskUseCase;
import com.btg.infrastructure.web.task.dto.response.PagedTaskResponse;
import com.btg.infrastructure.web.task.dto.response.TaskDetailResponse;
import com.btg.infrastructure.web.task.dto.response.TaskResponse;
import com.btg.infrastructure.web.task.dto.response.UserResponse;
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

    // GetTaskUseCase 변환
    TaskDetailResponse toDetailResponse(GetTaskUseCase.TaskDetailResult result);
    UserResponse toUserResponse(GetTaskUseCase.UserInfo userInfo);

    // ListTasksUseCase 변환
    TaskResponse toResponse(ListTasksUseCase.TaskSummary summary);
    UserResponse toUserResponse(ListTasksUseCase.UserInfo userInfo);
    List<TaskResponse> toResponseList(List<ListTasksUseCase.TaskSummary> summaries);
    PagedTaskResponse toPagedResponse(ListTasksUseCase.PagedTaskResult result);
}
