package com.btg.infrastructure.web.mapper;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyProgressSummaryResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.DailyStatResponse;
import com.btg.infrastructure.web.dailyprogress.dto.response.MyDailyProgressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DailyProgressResponseMapper {

    // GetDailyProgressUseCase 변환
    DailyProgressSummaryResponse toSummaryResponse(GetDailyProgressUseCase.DailyProgressSummaryResult result);
    DailyStatResponse toStatResponse(GetDailyProgressUseCase.DailyStat stat);
    List<DailyStatResponse> toStatResponseList(List<GetDailyProgressUseCase.DailyStat> stats);

    MyDailyProgressResponse toMyProgressResponse(GetDailyProgressUseCase.MyDailyProgressResult result);
    DailyProgressResponse toProgressResponse(GetDailyProgressUseCase.DailyRecord record);
    List<DailyProgressResponse> toProgressResponseList(List<GetDailyProgressUseCase.DailyRecord> records);

    // UpdateDailyProgressUseCase 변환
    DailyProgressResponse toProgressResponse(UpdateDailyProgressUseCase.DailyProgressResult result);
}
