package com.sky.service;

import com.sky.dto.DateDTO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface ReportService {
    TurnoverReportVO turnoverStatistics(DateDTO dto);

    UserReportVO userStatistics(DateDTO dto);

    SalesTop10ReportVO top10(DateDTO dto);
}
