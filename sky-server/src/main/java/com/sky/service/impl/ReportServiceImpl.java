package com.sky.service.impl;

import com.sky.constant.ReportConstant;
import com.sky.dto.DateDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.SalesVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(DateDTO dto) {
        List<LocalDate> days = beginToEnd(dto);
        List<Double> turnovers = new ArrayList<>();
        for (LocalDate day : days) {
            Map map = dateToDateTime(day);
            Integer status = Orders.COMPLETED;
            map.put(ReportConstant.STATUS,status);
            Double turnover = ordersMapper.getByDate(map);
            turnovers.add(turnover);
        }
        return TurnoverReportVO.builder().dateList(StringUtils.join(days,",")).turnoverList(StringUtils.join(turnovers,",")).build();
    }

    @Override
    public UserReportVO userStatistics(DateDTO dto) {
        List<LocalDate> days = beginToEnd(dto);
        List<Integer> newUsers = new ArrayList<>();
        List<Integer> allUsers = new ArrayList<>();
        for (LocalDate day : days) {
            Map map = dateToDateTime(day);
            Integer newUser = userMapper.getByDate(map);
            map.put(ReportConstant.BEGIN,null);
            Integer allUser = userMapper.getByDate(map);
            newUsers.add(newUser);
            allUsers.add(allUser);
        }
        return UserReportVO.builder().dateList(StringUtils.join(days,",")).newUserList(StringUtils.join(newUsers,",")).totalUserList(StringUtils.join(allUsers,",")).build();
    }

    @Override
    public SalesTop10ReportVO top10(DateDTO dto) {
        List<SalesVO> list = orderDetailMapper.getSalesTop10(dto);
        return SalesTop10ReportVO.builder().nameList(StringUtils.join(list.stream().map(SalesVO::getName).collect(Collectors.toList()), ",")).numberList(StringUtils.join(list.stream().map(SalesVO::getNumbers).collect(Collectors.toList()), ",")).build();
    }

    private List<LocalDate> beginToEnd(DateDTO dto){
        List<LocalDate> days = new ArrayList<>();
        LocalDate day = dto.getBegin();
        days.add(day);
        while (!day.equals(dto.getEnd())){
            day = day.plusDays(1);
            days.add(day);
        }
        return days;
    }

    private Map dateToDateTime(LocalDate day){
        LocalDateTime begin = LocalDateTime.of(day, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(day, LocalTime.MAX);
        Map map = new HashMap<>();
        map.put(ReportConstant.BEGIN,begin);
        map.put(ReportConstant.END,end);
        return map;
    }
}
