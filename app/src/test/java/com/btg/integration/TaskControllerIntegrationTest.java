package com.btg.integration;

import com.btg.core.application.port.in.task.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Task Controller Integration Tests")
class TaskControllerIntegrationTest extends IntegrationTestBase {
    // Use Cases are inherited from IntegrationTestBase

    @Test
    @DisplayName("POST /tasks - Success")
    void createTask_Success() throws Exception {
        // Given
        CreateTaskUseCase.UserInfo userInfo = new CreateTaskUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        CreateTaskUseCase.TaskResult taskResult = new CreateTaskUseCase.TaskResult(
            1L,
            1L,
            "Daily Coding Challenge",
            "Solve one algorithm problem daily",
            "RECRUITING",
            "2025-02-01",
            "2025-02-28",
            28,
            1,
            10,
            0.0,
            userInfo,
            "2025-01-01T00:00:00",
            "2025-01-01T00:00:00"
        );
        when(createTaskUseCase.createTask(any())).thenReturn(taskResult);

        // When & Then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "groupId": 1,
                        "title": "Daily Coding Challenge",
                        "description": "Solve one algorithm problem daily",
                        "startDate": "2025-02-01",
                        "endDate": "2025-02-28",
                        "maxParticipants": 10
                    }
                    """))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.groupId").value(1))
            .andExpect(jsonPath("$.title").value("Daily Coding Challenge"))
            .andExpect(jsonPath("$.description").value("Solve one algorithm problem daily"))
            .andExpect(jsonPath("$.status").value("RECRUITING"))
            .andExpect(jsonPath("$.startDate").value("2025-02-01"))
            .andExpect(jsonPath("$.endDate").value("2025-02-28"))
            .andExpect(jsonPath("$.totalDays").value(28))
            .andExpect(jsonPath("$.participantCount").value(1))
            .andExpect(jsonPath("$.maxParticipants").value(10))
            .andExpect(jsonPath("$.overallCompletionRate").value(0.0))
            .andExpect(jsonPath("$.createdBy.id").value(1))
            .andExpect(jsonPath("$.createdBy.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /tasks - Validation Error (Short Title)")
    void createTask_ValidationError_ShortTitle() throws Exception {
        // When & Then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "groupId": 1,
                        "title": "A",
                        "startDate": "2025-02-01",
                        "endDate": "2025-02-28"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks - Validation Error (Missing Start Date)")
    void createTask_ValidationError_MissingStartDate() throws Exception {
        // When & Then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "groupId": 1,
                        "title": "Valid Title",
                        "endDate": "2025-02-28"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /tasks - Success")
    void listTasks_Success() throws Exception {
        // Given
        ListTasksUseCase.UserInfo userInfo = new ListTasksUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        ListTasksUseCase.TaskSummary task1 = new ListTasksUseCase.TaskSummary(
            1L, 1L, "Task 1", "Description 1", "RECRUITING",
            "2025-02-01", "2025-02-28", 28, 5, 10, 0.0,
            userInfo, "2025-01-01T00:00:00", "2025-01-01T00:00:00"
        );
        ListTasksUseCase.TaskSummary task2 = new ListTasksUseCase.TaskSummary(
            2L, 1L, "Task 2", "Description 2", "IN_PROGRESS",
            "2025-01-15", "2025-02-15", 31, 3, 20, 45.5,
            userInfo, "2025-01-10T00:00:00", "2025-01-15T00:00:00"
        );
        ListTasksUseCase.PagedTaskResult pagedResult = new ListTasksUseCase.PagedTaskResult(
            List.of(task1, task2),
            2,
            1,
            0,
            20
        );
        when(listTasksUseCase.listTasks(any())).thenReturn(pagedResult);

        // When & Then
        mockMvc.perform(get("/tasks")
                .param("status", "ALL")
                .param("page", "0")
                .param("size", "20"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].title").value("Task 1"))
            .andExpect(jsonPath("$.content[0].status").value("RECRUITING"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].title").value("Task 2"))
            .andExpect(jsonPath("$.content[1].status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /tasks/{taskId} - Success")
    void getTask_Success() throws Exception {
        // Given
        GetTaskUseCase.UserInfo userInfo = new GetTaskUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        GetTaskUseCase.TaskDetailResult taskDetailResult = new GetTaskUseCase.TaskDetailResult(
            1L,
            1L,
            "Daily Coding Challenge",
            "Solve one algorithm problem daily",
            "IN_PROGRESS",
            "2025-02-01",
            "2025-02-28",
            28,
            5,
            10,
            75.5,
            userInfo,
            "2025-01-01T00:00:00",
            "2025-01-15T00:00:00",
            true,
            80.0
        );
        when(getTaskUseCase.getTask(eq(1L), any())).thenReturn(taskDetailResult);

        // When & Then
        mockMvc.perform(get("/tasks/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.groupId").value(1))
            .andExpect(jsonPath("$.title").value("Daily Coding Challenge"))
            .andExpect(jsonPath("$.description").value("Solve one algorithm problem daily"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.startDate").value("2025-02-01"))
            .andExpect(jsonPath("$.endDate").value("2025-02-28"))
            .andExpect(jsonPath("$.totalDays").value(28))
            .andExpect(jsonPath("$.participantCount").value(5))
            .andExpect(jsonPath("$.maxParticipants").value(10))
            .andExpect(jsonPath("$.overallCompletionRate").value(75.5))
            .andExpect(jsonPath("$.isParticipating").value(true))
            .andExpect(jsonPath("$.myCompletionRate").value(80.0));
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} - Success")
    void updateTask_Success() throws Exception {
        // Given
        UpdateTaskUseCase.UserInfo userInfo = new UpdateTaskUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        UpdateTaskUseCase.TaskResult taskResult = new UpdateTaskUseCase.TaskResult(
            1L,
            1L,
            "Updated Title",
            "Updated description",
            "RECRUITING",
            "2025-02-01",
            "2025-02-28",
            28,
            5,
            15,
            0.0,
            userInfo,
            "2025-01-01T00:00:00",
            "2025-01-20T00:00:00"
        );
        when(updateTaskUseCase.updateTask(any())).thenReturn(taskResult);

        // When & Then
        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Updated Title",
                        "description": "Updated description",
                        "maxParticipants": 15
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("Updated description"))
            .andExpect(jsonPath("$.maxParticipants").value(15));
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} - Validation Error (Long Description)")
    void updateTask_ValidationError_LongDescription() throws Exception {
        // When & Then
        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Valid Title",
                        "description": "%s"
                    }
                    """.formatted("A".repeat(1001))))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} - Success")
    void deleteTask_Success() throws Exception {
        // Given
        doNothing().when(deleteTaskUseCase).deleteTask(any());

        // When & Then
        mockMvc.perform(delete("/tasks/1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}
