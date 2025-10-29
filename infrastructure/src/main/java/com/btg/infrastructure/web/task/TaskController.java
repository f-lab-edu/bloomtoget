package com.btg.infrastructure.web.task;

import com.btg.core.application.port.in.task.*;
import com.btg.infrastructure.web.task.dto.request.CreateTaskRequest;
import com.btg.infrastructure.web.task.dto.request.UpdateTaskRequest;
import com.btg.infrastructure.web.task.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

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

        TaskResponse response = new TaskResponse(
            result.id(),
            result.groupId(),
            result.title(),
            result.description(),
            result.status(),
            result.startDate(),
            result.endDate(),
            result.totalDays(),
            result.participantCount(),
            result.maxParticipants(),
            result.overallCompletionRate(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt(),
            result.updatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedTaskResponse> listTasks(
        @RequestParam(required = false) Long groupId,
        @RequestParam(defaultValue = "ALL") String status,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        ListTasksUseCase.ListTasksQuery query = new ListTasksUseCase.ListTasksQuery(
            userId,
            groupId,
            status,
            page,
            size
        );

        ListTasksUseCase.PagedTaskResult result = listTasksUseCase.listTasks(query);

        PagedTaskResponse response = new PagedTaskResponse(
            result.content().stream()
                .map(task -> new TaskResponse(
                    task.id(),
                    task.groupId(),
                    task.title(),
                    task.description(),
                    task.status(),
                    task.startDate(),
                    task.endDate(),
                    task.totalDays(),
                    task.participantCount(),
                    task.maxParticipants(),
                    task.overallCompletionRate(),
                    new UserResponse(
                        task.createdBy().id(),
                        task.createdBy().email(),
                        task.createdBy().name()
                    ),
                    task.createdAt(),
                    task.updatedAt()
                ))
                .collect(Collectors.toList()),
            result.totalElements(),
            result.totalPages(),
            result.page(),
            result.size()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailResponse> getTask(@PathVariable Long taskId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        GetTaskUseCase.TaskDetailResult result = getTaskUseCase.getTask(taskId, userId);

        TaskDetailResponse response = new TaskDetailResponse(
            result.id(),
            result.groupId(),
            result.title(),
            result.description(),
            result.status(),
            result.startDate(),
            result.endDate(),
            result.totalDays(),
            result.participantCount(),
            result.maxParticipants(),
            result.overallCompletionRate(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt(),
            result.updatedAt(),
            result.isParticipating(),
            result.myCompletionRate()
        );

        return ResponseEntity.ok(response);
    }

    // TODO: PATCH /tasks/{taskId}/status - Task 상태 변경

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable Long taskId,
        @Valid @RequestBody UpdateTaskRequest request
    ) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        UpdateTaskUseCase.UpdateTaskCommand command = new UpdateTaskUseCase.UpdateTaskCommand(
            taskId,
            userId,
            request.title(),
            request.description(),
            request.maxParticipants()
        );

        UpdateTaskUseCase.TaskResult result = updateTaskUseCase.updateTask(command);

        TaskResponse response = new TaskResponse(
            result.id(),
            result.groupId(),
            result.title(),
            result.description(),
            result.status(),
            result.startDate(),
            result.endDate(),
            result.totalDays(),
            result.participantCount(),
            result.maxParticipants(),
            result.overallCompletionRate(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt(),
            result.updatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        DeleteTaskUseCase.DeleteTaskCommand command = new DeleteTaskUseCase.DeleteTaskCommand(
            taskId,
            userId
        );

        deleteTaskUseCase.deleteTask(command);

        return ResponseEntity.noContent().build();
    }

    // TODO: GET /tasks/{taskId}/members - Task 참가자 목록
    // TODO: POST /tasks/{taskId}/members - Task 참가
    // TODO: DELETE /tasks/{taskId}/members/me - Task 참가 취소
    // TODO: GET /tasks/{taskId}/progress - Task 전체 진행률
}
