package com.btg.core.application.port.out.dailyprogress;

public interface DeleteDailyProgressPort {

    void deleteByTaskMemberId(Long taskMemberId);

    void deleteAllByTaskId(Long taskId);
}