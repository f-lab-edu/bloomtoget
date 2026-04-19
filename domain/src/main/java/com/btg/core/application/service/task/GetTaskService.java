package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.GetTaskUseCase;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
public class GetTaskService implements GetTaskUseCase {

    private final LoadTaskPort loadTaskPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;
    private final LoadUserPort loadUserPort;
    private final Executor executor;

    public GetTaskService(
            LoadTaskPort loadTaskPort,
            LoadTaskMemberPort loadTaskMemberPort,
            LoadDailyProgressPort loadDailyProgressPort,
            LoadUserPort loadUserPort,
            @Qualifier("taskDetailExecutor") Executor executor) {
        this.loadTaskPort = loadTaskPort;
        this.loadTaskMemberPort = loadTaskMemberPort;
        this.loadDailyProgressPort = loadDailyProgressPort;
        this.loadUserPort = loadUserPort;
        this.executor = executor;
    }

    @Override
    public TaskDetailResult getTask(Long taskId, Long userId) {
        // 1. Task 기본 정보 조회 (필수, 다른 조회에 의존성 없음)
        CompletableFuture<LoadTaskPort.Task> taskFuture = CompletableFuture
                .supplyAsync(() -> loadTaskPort.loadById(taskId)
                        .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId)), executor);

        // 2. 참가자 수 조회
        CompletableFuture<Integer> participantCountFuture = CompletableFuture
                .supplyAsync(() -> loadTaskMemberPort.countByTaskId(taskId), executor);

        // 3. 전체 완료 수 조회 (overallCompletionRate 계산용)
        CompletableFuture<Integer> completedCountFuture = CompletableFuture
                .supplyAsync(() -> loadDailyProgressPort.countCompletedByTaskId(taskId), executor);

        // 4. 전체 진행 레코드 수 조회 (overallCompletionRate 계산용)
        CompletableFuture<Integer> totalCountFuture = CompletableFuture
                .supplyAsync(() -> loadDailyProgressPort.countTotalByTaskId(taskId), executor);

        // 5. 사용자 참가 여부 조회
        CompletableFuture<Boolean> isParticipatingFuture = CompletableFuture
                .supplyAsync(() -> loadTaskMemberPort.existsByTaskIdAndUserId(taskId, userId), executor);

        // 6. 사용자의 TaskMember 조회 (myCompletionRate 계산용)
        CompletableFuture<LoadTaskMemberPort.TaskMember> taskMemberFuture = CompletableFuture
                .supplyAsync(() -> loadTaskMemberPort.loadByTaskIdAndUserId(taskId, userId).orElse(null), executor);

        // 7. Task 조회 완료 후 생성자 정보 조회 (의존성: taskFuture)
        CompletableFuture<LoadUserPort.User> createdByUserFuture = taskFuture
                .thenApplyAsync(task -> loadUserPort.loadById(task.createdByUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + task.createdByUserId())), executor);

        // 8. TaskMember 조회 완료 후 myCompletionRate 계산 (의존성: taskMemberFuture, taskFuture)
        CompletableFuture<Double> myCompletionRateFuture = taskMemberFuture
                .thenCombineAsync(taskFuture, (taskMember, task) -> {
                    if (taskMember == null) {
                        return null;
                    }
                    int myCompletedCount = loadDailyProgressPort.countCompletedByTaskMemberId(taskMember.id());
                    int totalDays = task.totalDays();
                    return totalDays > 0 ? (double) myCompletedCount / totalDays * 100 : 0.0;
                }, executor);

        // 모든 Future 완료 대기 및 결과 조합
        try {
            CompletableFuture.allOf(
                    taskFuture,
                    participantCountFuture,
                    completedCountFuture,
                    totalCountFuture,
                    isParticipatingFuture,
                    createdByUserFuture,
                    myCompletionRateFuture
            ).orTimeout(5, TimeUnit.SECONDS).join();

            LoadTaskPort.Task task = taskFuture.join();
            Integer participantCount = participantCountFuture.join();
            Integer completedCount = completedCountFuture.join();
            Integer totalCount = totalCountFuture.join();
            Boolean isParticipating = isParticipatingFuture.join();
            LoadUserPort.User createdByUser = createdByUserFuture.join();
            Double myCompletionRate = myCompletionRateFuture.join();

            double overallCompletionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0.0;

            return new TaskDetailResult(
                    task.id(),
                    task.groupId(),
                    task.title(),
                    task.description(),
                    task.status(),
                    task.startDate(),
                    task.endDate(),
                    task.totalDays(),
                    participantCount,
                    task.maxParticipants(),
                    Math.round(overallCompletionRate * 100.0) / 100.0,
                    new UserInfo(createdByUser.id(), createdByUser.email(), createdByUser.name()),
                    String.valueOf(task.createdAt()),
                    String.valueOf(task.updatedAt()),
                    isParticipating,
                    myCompletionRate != null ? Math.round(myCompletionRate * 100.0) / 100.0 : null
            );
        } catch (Exception e) {
            log.error("Failed to get task detail: taskId={}, userId={}", taskId, userId, e);
            if (e.getCause() instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e.getCause();
            }
            throw new RuntimeException("Failed to get task detail", e);
        }
    }
}
