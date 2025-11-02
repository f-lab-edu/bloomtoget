package com.btg.infrastructure.web.dailyprogress;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.infrastructure.web.dailyprogress.dto.request.UpdateDailyProgressRequest;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressSummaryResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyStatResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.MyDailyProgressResponse;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks/{taskId}/daily-progress")
@RequiredArgsConstructor
public class DailyProgressController {

    private final GetDailyProgressUseCase getDailyProgressUseCase;
    private final UpdateDailyProgressUseCase updateDailyProgressUseCase;

    @GetMapping
    public ResponseEntity<DailyProgressSummaryResponse> getDailyProgressSummary(@PathVariable Long taskId) {
        GetDailyProgressUseCase.DailyProgressSummaryResult result =
            getDailyProgressUseCase.getDailyProgressSummary(taskId);

        DailyProgressSummaryResponse response = new DailyProgressSummaryResponse(
            result.taskId(),
            result.startDate(),
            result.endDate(),
            result.dailyStats().stream()
                .map(stat -> new DailyStatResponse(
                    stat.date(),
                    stat.completedCount(),
                    stat.totalParticipants(),
                    stat.completionRate()
                ))
                .collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MyDailyProgressResponse> getMyDailyProgress(@PathVariable Long taskId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        GetDailyProgressUseCase.MyDailyProgressResult result =
            getDailyProgressUseCase.getMyDailyProgress(taskId, userId);

        MyDailyProgressResponse response = new MyDailyProgressResponse(
            result.taskId(),
            result.userId(),
            result.dailyRecords().stream()
                .map(record -> new DailyProgressResponse(
                    record.date(),
                    record.completed(),
                    record.completedAt()
                ))
                .collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
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

        DailyProgressResponse response = new DailyProgressResponse(
            result.date(),
            result.completed(),
            result.completedAt()
        );

        return ResponseEntity.ok(response);
    }
}
