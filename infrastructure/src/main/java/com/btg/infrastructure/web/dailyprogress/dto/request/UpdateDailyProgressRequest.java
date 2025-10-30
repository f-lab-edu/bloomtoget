package com.btg.infrastructure.web.dailyprogress.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateDailyProgressRequest(
    @NotNull(message = "Completed status is required")
    Boolean completed
) {}
