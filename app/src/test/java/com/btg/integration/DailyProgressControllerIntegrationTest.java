package com.btg.integration;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("DailyProgress Controller Integration Tests")
class DailyProgressControllerIntegrationTest extends IntegrationTestBase {
    // Use Cases are inherited from IntegrationTestBase

    @Test
    @DisplayName("GET /tasks/{taskId}/daily-progress - Success")
    void getDailyProgressSummary_Success() throws Exception {
        // Given
        GetDailyProgressUseCase.DailyStat stat1 = new GetDailyProgressUseCase.DailyStat(
            "2025-02-01", 3, 5, 60.0
        );
        GetDailyProgressUseCase.DailyStat stat2 = new GetDailyProgressUseCase.DailyStat(
            "2025-02-02", 4, 5, 80.0
        );
        GetDailyProgressUseCase.DailyProgressSummaryResult summaryResult =
            new GetDailyProgressUseCase.DailyProgressSummaryResult(
                1L,
                "2025-02-01",
                "2025-02-28",
                List.of(stat1, stat2)
            );
        when(getDailyProgressUseCase.getDailyProgressSummary(1L)).thenReturn(summaryResult);

        // When & Then
        mockMvc.perform(get("/tasks/1/daily-progress"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(1))
            .andExpect(jsonPath("$.startDate").value("2025-02-01"))
            .andExpect(jsonPath("$.endDate").value("2025-02-28"))
            .andExpect(jsonPath("$.dailyStats.length()").value(2))
            .andExpect(jsonPath("$.dailyStats[0].date").value("2025-02-01"))
            .andExpect(jsonPath("$.dailyStats[0].completedCount").value(3))
            .andExpect(jsonPath("$.dailyStats[0].totalParticipants").value(5))
            .andExpect(jsonPath("$.dailyStats[0].completionRate").value(60.0))
            .andExpect(jsonPath("$.dailyStats[1].date").value("2025-02-02"))
            .andExpect(jsonPath("$.dailyStats[1].completedCount").value(4))
            .andExpect(jsonPath("$.dailyStats[1].completionRate").value(80.0));
    }

    @Test
    @DisplayName("GET /tasks/{taskId}/daily-progress/me - Success")
    void getMyDailyProgress_Success() throws Exception {
        // Given
        GetDailyProgressUseCase.DailyRecord record1 = new GetDailyProgressUseCase.DailyRecord(
            "2025-02-01", true, "2025-02-01T10:30:00"
        );
        GetDailyProgressUseCase.DailyRecord record2 = new GetDailyProgressUseCase.DailyRecord(
            "2025-02-02", false, null
        );
        GetDailyProgressUseCase.DailyRecord record3 = new GetDailyProgressUseCase.DailyRecord(
            "2025-02-03", true, "2025-02-03T15:45:00"
        );
        GetDailyProgressUseCase.MyDailyProgressResult myProgressResult =
            new GetDailyProgressUseCase.MyDailyProgressResult(
                1L,
                1L,
                List.of(record1, record2, record3)
            );
        when(getDailyProgressUseCase.getMyDailyProgress(eq(1L), any())).thenReturn(myProgressResult);

        // When & Then
        mockMvc.perform(get("/tasks/1/daily-progress/me"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.dailyRecords.length()").value(3))
            .andExpect(jsonPath("$.dailyRecords[0].date").value("2025-02-01"))
            .andExpect(jsonPath("$.dailyRecords[0].completed").value(true))
            .andExpect(jsonPath("$.dailyRecords[0].completedAt").value("2025-02-01T10:30:00"))
            .andExpect(jsonPath("$.dailyRecords[1].date").value("2025-02-02"))
            .andExpect(jsonPath("$.dailyRecords[1].completed").value(false))
            .andExpect(jsonPath("$.dailyRecords[1].completedAt").isEmpty())
            .andExpect(jsonPath("$.dailyRecords[2].date").value("2025-02-03"))
            .andExpect(jsonPath("$.dailyRecords[2].completed").value(true))
            .andExpect(jsonPath("$.dailyRecords[2].completedAt").value("2025-02-03T15:45:00"));
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/daily-progress/{date} - Success (Completed True)")
    void updateDailyProgress_Success_CompletedTrue() throws Exception {
        // Given
        UpdateDailyProgressUseCase.DailyProgressResult progressResult =
            new UpdateDailyProgressUseCase.DailyProgressResult(
                "2025-02-01",
                true,
                "2025-02-01T10:30:00"
            );
        when(updateDailyProgressUseCase.updateDailyProgress(any())).thenReturn(progressResult);

        // When & Then
        mockMvc.perform(patch("/tasks/1/daily-progress/2025-02-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "completed": true
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value("2025-02-01"))
            .andExpect(jsonPath("$.completed").value(true))
            .andExpect(jsonPath("$.completedAt").value("2025-02-01T10:30:00"));
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/daily-progress/{date} - Success (Completed False)")
    void updateDailyProgress_Success_CompletedFalse() throws Exception {
        // Given
        UpdateDailyProgressUseCase.DailyProgressResult progressResult =
            new UpdateDailyProgressUseCase.DailyProgressResult(
                "2025-02-01",
                false,
                null
            );
        when(updateDailyProgressUseCase.updateDailyProgress(any())).thenReturn(progressResult);

        // When & Then
        mockMvc.perform(patch("/tasks/1/daily-progress/2025-02-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "completed": false
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value("2025-02-01"))
            .andExpect(jsonPath("$.completed").value(false))
            .andExpect(jsonPath("$.completedAt").isEmpty());
    }

    @Test
    @DisplayName("PATCH /tasks/{taskId}/daily-progress/{date} - Validation Error (Missing Completed)")
    void updateDailyProgress_ValidationError_MissingCompleted() throws Exception {
        // When & Then
        mockMvc.perform(patch("/tasks/1/daily-progress/2025-02-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}
