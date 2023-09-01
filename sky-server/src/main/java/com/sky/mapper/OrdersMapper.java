package com.sky.mapper;

import com.sky.dto.HistoryOrdersDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrdersMapper {

    void insert(Orders order);

    List<OrderVO> getByUserID(HistoryOrdersDTO historyOrdersDTO);
}
