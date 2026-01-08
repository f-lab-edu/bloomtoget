package com.btg.core.application.service.dailyprogress;

import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import org.springframework.stereotype.Service;

/**
 * UpdateDailyProgressUseCase 구현체
 *
 * TODO: 실제 비즈니스 로직 구현 필요
 * - DailyProgress 업데이트 로직
 * - 날짜 유효성 검증
 * - Outbound Port 연결 (SaveDailyProgressPort 등)
 */
@Service
public class UpdateDailyProgressService implements UpdateDailyProgressUseCase {

    @Override
    public DailyProgressResult updateDailyProgress(UpdateDailyProgressCommand command) {
        // TODO: Implement actual business logic
        // 1. Validate command (already done in record constructor)
        // 2. Verify task exists and status is not RECRUITING
        // 3. Verify user is participating in the task
        // 4. Verify date is within task date range
        // 5. Update or create daily progress record
        // 6. Auto-set completedAt when completed=true
        // 7. Return updated result
        throw new UnsupportedOperationException("updateDailyProgress not implemented yet");
    }
}
