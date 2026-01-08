package com.btg.core.application.service.dailyprogress;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import org.springframework.stereotype.Service;

/**
 * GetDailyProgressUseCase 구현체
 *
 * TODO: 실제 비즈니스 로직 구현 필요
 * - DailyProgress 조회 로직
 * - 통계 계산 로직
 * - Outbound Port 연결 (LoadDailyProgressPort 등)
 */
@Service
public class GetDailyProgressService implements GetDailyProgressUseCase {

    @Override
    public DailyProgressSummaryResult getDailyProgressSummary(Long taskId) {
        // TODO: Implement actual business logic
        // 1. Validate taskId
        // 2. Load task and verify it exists
        // 3. Load all daily progress records for the task
        // 4. Calculate statistics (completion rate, etc.)
        // 5. Return summary result
        throw new UnsupportedOperationException("getDailyProgressSummary not implemented yet");
    }

    @Override
    public MyDailyProgressResult getMyDailyProgress(Long taskId, Long userId) {
        // TODO: Implement actual business logic
        // 1. Validate taskId and userId
        // 2. Load task and verify it exists
        // 3. Verify user is participating in the task
        // 4. Load user's daily progress records
        // 5. Return user's progress result
        throw new UnsupportedOperationException("getMyDailyProgress not implemented yet");
    }
}
