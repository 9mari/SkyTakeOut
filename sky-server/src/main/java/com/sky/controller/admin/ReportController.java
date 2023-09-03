package com.sky.controller.admin;


import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.DateDTO;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/admin/report")
@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(DateDTO dto) {
        log.info("/turnoverStatistics 被请求了");
        TurnoverReportVO vo = reportService.turnoverStatistics(dto);
        return Result.success(vo);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(DateDTO dto) {
        UserReportVO vo = reportService.userStatistics(dto);
        return Result.success(vo);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(DateDTO dto) {
        SalesTop10ReportVO vo = reportService.top10(dto);
        return Result.success(vo);
    }
}
