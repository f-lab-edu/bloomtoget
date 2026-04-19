package com.btg.infrastructure.persistence.dailyprogress.adapter;

import com.btg.core.application.port.out.dailyprogress.DeleteDailyProgressPort;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.dailyprogress.SaveDailyProgressPort;
import com.btg.infrastructure.persistence.dailyprogress.entity.DailyProgressJpaEntity;
import com.btg.infrastructure.persistence.dailyprogress.repository.DailyProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DailyProgressPersistenceAdapter implements SaveDailyProgressPort, LoadDailyProgressPort, DeleteDailyProgressPort {

    private final DailyProgressJpaRepository dailyProgressRepository;

    @Override
    public void saveAll(Long taskMemberId, List<DailyProgressEntry> entries) {
        List<DailyProgressJpaEntity> entities = entries.stream()
            .map(entry -> new DailyProgressJpaEntity(
                taskMemberId,
                LocalDate.parse(entry.date()),
                entry.completed()
            ))
            .collect(Collectors.toList());

        dailyProgressRepository.saveAll(entities);
    }

    @Override
    public List<DailyProgress> loadByTaskMemberId(Long taskMemberId) {
        return dailyProgressRepository.findByTaskMemberId(taskMemberId).stream()
            .map(this::toDailyProgress)
            .collect(Collectors.toList());
    }

    @Override
    public int countCompletedByTaskMemberId(Long taskMemberId) {
        return dailyProgressRepository.countByTaskMemberIdAndCompletedTrue(taskMemberId);
    }

    @Override
    public int countCompletedByTaskId(Long taskId) {
        return dailyProgressRepository.countCompletedByTaskId(taskId);
    }

    @Override
    public int countTotalByTaskId(Long taskId) {
        return dailyProgressRepository.countTotalByTaskId(taskId);
    }

    @Override
    @Transactional
    public void deleteByTaskMemberId(Long taskMemberId) {
        dailyProgressRepository.deleteByTaskMemberId(taskMemberId);
    }

    @Override
    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        dailyProgressRepository.deleteAllByTaskId(taskId);
    }

    private DailyProgress toDailyProgress(DailyProgressJpaEntity entity) {
        return new DailyProgress(
            entity.getId(),
            entity.getTaskMemberId(),
            entity.getDate().toString(),
            entity.getCompleted(),
            entity.getCompletedAt() != null
                ? entity.getCompletedAt().toInstant(ZoneOffset.UTC).toString()
                : null
        );
    }
}
