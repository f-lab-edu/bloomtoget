package com.btg.infrastructure.web.task.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
    @NotNull(message = "Group ID is required")
    Long groupId,

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    String title,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotBlank(message = "Start date is required")
    String startDate,  // Format: "YYYY-MM-DD"

    @NotBlank(message = "End date is required")
    String endDate,    // Format: "YYYY-MM-DD"

    @Min(value = 1, message = "Max participants must be at least 1")
    @Max(value = 100, message = "Max participants must not exceed 100")
    Integer maxParticipants
) {}
