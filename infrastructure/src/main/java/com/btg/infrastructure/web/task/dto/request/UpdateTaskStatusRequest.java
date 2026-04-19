package com.btg.infrastructure.web.task.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskStatusRequest(
    @NotBlank(message = "Status is required")
    String status
) {}
