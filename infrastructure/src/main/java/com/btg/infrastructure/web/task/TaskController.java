package com.btg.infrastructure.web.task;

import com.btg.core.application.port.in.task.*;
import com.btg.infrastructure.security.SecurityContextUtil;
import com.btg.infrastructure.web.mapper.TaskResponseMapper;
import com.btg.infrastructure.web.task.dto.request.CreateTaskRequest;
import com.btg.infrastructure.web.task.dto.request.UpdateTaskRequest;
import com.btg.infrastructure.web.task.dto.request.UpdateTaskStatusRequest;
import com.btg.infrastructure.web.task.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final JoinTaskUseCase joinTaskUseCase;
    private final LeaveTaskUseCase leaveTaskUseCase;
    private final ListTaskMembersUseCase listTaskMembersUseCase;
    private final GetTaskProgressUseCase getTaskProgressUseCase;
    private final TaskResponseMapper taskResponseMapper;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        CreateTaskUseCase.CreateTaskCommand command = new CreateTaskUseCase.CreateTaskCommand(
            userId,
            request.groupId(),
            request.title(),
            request.description(),
            request.startDate(),
            request.endDate(),
            request.maxParticipants()
        );

        CreateTaskUseCase.TaskResult result = createTaskUseCase.createTask(command);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(taskResponseMapper.toResponse(result));
    }

    @GetMapping
    public ResponseEntity<PagedTaskResponse> listTasks(
        @RequestParam(required = false) Long groupId,
        @RequestParam(defaultValue = "ALL") String status,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        ListTasksUseCase.ListTasksQuery query = new ListTasksUseCase.ListTasksQuery(
            userId,
            groupId,
            status,
            page,
            size
        );

        ListTasksUseCase.PagedTaskResult result = listTasksUseCase.listTasks(query);

        return ResponseEntity.ok(taskResponseMapper.toPagedResponse(result));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailResponse> getTask(@PathVariable Long taskId) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        GetTaskUseCase.TaskDetailResult result = getTaskUseCase.getTask(taskId, userId);

        return ResponseEntity.ok(taskResponseMapper.toDetailResponse(result));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
        @PathVariable Long taskId,
        @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        UpdateTaskStatusUseCase.UpdateStatusCommand command = new UpdateTaskStatusUseCase.UpdateStatusCommand(
            taskId,
            userId,
            request.status()
        );

        UpdateTaskStatusUseCase.TaskResult result = updateTaskStatusUseCase.updateStatus(command);

        return ResponseEntity.ok(taskResponseMapper.toResponse(result));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable Long taskId,
        @Valid @RequestBody UpdateTaskRequest request
    ) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        UpdateTaskUseCase.UpdateTaskCommand command = new UpdateTaskUseCase.UpdateTaskCommand(
            taskId,
            userId,
            request.title(),
            request.description(),
            request.maxParticipants()
        );

        UpdateTaskUseCase.TaskResult result = updateTaskUseCase.updateTask(command);

        return ResponseEntity.ok(taskResponseMapper.toResponse(result));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        DeleteTaskUseCase.DeleteTaskCommand command = new DeleteTaskUseCase.DeleteTaskCommand(
            taskId,
            userId
        );

        deleteTaskUseCase.deleteTask(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/members")
    public ResponseEntity<TaskMemberListResponse> listTaskMembers(@PathVariable Long taskId) {
        ListTaskMembersUseCase.ListTaskMembersQuery query =
            new ListTaskMembersUseCase.ListTaskMembersQuery(taskId);

        ListTaskMembersUseCase.TaskMemberListResult result = listTaskMembersUseCase.listTaskMembers(query);

        return ResponseEntity.ok(taskResponseMapper.toMemberListResponse(result));
    }

    @PostMapping("/{taskId}/members")
    public ResponseEntity<TaskMemberResponse> joinTask(@PathVariable Long taskId) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        JoinTaskUseCase.JoinTaskCommand command = new JoinTaskUseCase.JoinTaskCommand(
            taskId,
            userId
        );

        JoinTaskUseCase.TaskMemberResult result = joinTaskUseCase.joinTask(command);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(taskResponseMapper.toMemberResponse(result));
    }

    @DeleteMapping("/{taskId}/members/me")
    public ResponseEntity<Void> leaveTask(@PathVariable Long taskId) {
        Long userId = SecurityContextUtil.getCurrentUserId();

        LeaveTaskUseCase.LeaveTaskCommand command = new LeaveTaskUseCase.LeaveTaskCommand(
            taskId,
            userId
        );

        leaveTaskUseCase.leaveTask(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/progress")
    public ResponseEntity<TaskProgressResponse> getTaskProgress(@PathVariable Long taskId) {
        GetTaskProgressUseCase.TaskProgressResult result = getTaskProgressUseCase.getTaskProgress(taskId);

        return ResponseEntity.ok(taskResponseMapper.toProgressResponse(result));
    }
}
