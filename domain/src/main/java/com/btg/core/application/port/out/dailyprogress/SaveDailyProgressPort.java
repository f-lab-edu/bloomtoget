package com.btg.core.application.port.out.dailyprogress;

import java.util.List;

public interface SaveDailyProgressPort {

    void saveAll(Long taskMemberId, List<DailyProgressEntry> entries);

    record DailyProgressEntry(String date, Boolean completed) {}
}
