package com.btg.infrastructure.persistence.dailyprogress.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "daily_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"task_member_id", "date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyProgressJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_member_id", nullable = false)
    private Long taskMemberId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean completed;

    @Column
    private LocalDateTime completedAt;

    public DailyProgressJpaEntity(Long taskMemberId, LocalDate date, Boolean completed) {
        this.taskMemberId = taskMemberId;
        this.date = date;
        this.completed = completed;
    }

    public void markCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    public void markIncomplete() {
        this.completed = false;
        this.completedAt = null;
    }
}
