package com.btg.infrastructure.web.dailyprogress;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import com.btg.infrastructure.web.dailyprogress.dto.request.UpdateDailyProgressRequest;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressSummaryResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.MyDailyProgressResponse;
import com.btg.infrastructure.web.mapper.DailyProgressResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks/{taskId}/daily-progress")
@RequiredArgsConstructor
public class DailyProgressController {

    private final GetDailyProgressUseCase getDailyProgressUseCase;
    private final UpdateDailyProgressUseCase updateDailyProgressUseCase;
    private final DailyProgressResponseMapper dailyProgressResponseMapper;

    @GetMapping
    public ResponseEntity<DailyProgressSummaryResponse> getDailyProgressSummary(@PathVariable Long taskId) {
        GetDailyProgressUseCase.DailyProgressSummaryResult result =
            getDailyProgressUseCase.getDailyProgressSummary(taskId);

        return ResponseEntity.ok(dailyProgressResponseMapper.toSummaryResponse(result));
    }

    @GetMapping("/me")
    public ResponseEntity<MyDailyProgressResponse> getMyDailyProgress(@PathVariable Long taskId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        GetDailyProgressUseCase.MyDailyProgressResult result =
            getDailyProgressUseCase.getMyDailyProgress(taskId, userId);

        return ResponseEntity.ok(dailyProgressResponseMapper.toMyProgressResponse(result));
    }

    @PatchMapping("/{date}")
    public ResponseEntity<DailyProgressResponse> updateDailyProgress(
        @PathVariable Long taskId,
        @PathVariable String date,
        @Valid @RequestBody UpdateDailyProgressRequest request
    ) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        UpdateDailyProgressUseCase.UpdateDailyProgressCommand command =
            new UpdateDailyProgressUseCase.UpdateDailyProgressCommand(
                taskId,
                userId,
                date,
                request.completed()
            );

        UpdateDailyProgressUseCase.DailyProgressResult result =
            updateDailyProgressUseCase.updateDailyProgress(command);

        return ResponseEntity.ok(dailyProgressResponseMapper.toProgressResponse(result));
    }
}
