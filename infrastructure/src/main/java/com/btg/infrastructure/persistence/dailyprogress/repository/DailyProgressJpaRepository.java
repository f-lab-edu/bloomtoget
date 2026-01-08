package com.btg.infrastructure.persistence.dailyprogress.repository;

import com.btg.infrastructure.persistence.dailyprogress.entity.DailyProgressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyProgressJpaRepository extends JpaRepository<DailyProgressJpaEntity, Long> {

    List<DailyProgressJpaEntity> findByTaskMemberId(Long taskMemberId);

    int countByTaskMemberIdAndCompletedTrue(Long taskMemberId);

    @Query("SELECT COUNT(dp) FROM DailyProgressJpaEntity dp " +
           "WHERE dp.taskMemberId IN (SELECT tm.id FROM TaskMemberJpaEntity tm WHERE tm.taskId = :taskId) " +
           "AND dp.completed = true")
    int countCompletedByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT COUNT(dp) FROM DailyProgressJpaEntity dp " +
           "WHERE dp.taskMemberId IN (SELECT tm.id FROM TaskMemberJpaEntity tm WHERE tm.taskId = :taskId)")
    int countTotalByTaskId(@Param("taskId") Long taskId);

    void deleteByTaskMemberId(Long taskMemberId);

    @Query("DELETE FROM DailyProgressJpaEntity dp " +
           "WHERE dp.taskMemberId IN (SELECT tm.id FROM TaskMemberJpaEntity tm WHERE tm.taskId = :taskId)")
    @org.springframework.data.jpa.repository.Modifying
    void deleteAllByTaskId(@Param("taskId") Long taskId);
}
