package com.sky.mapper;

import com.sky.dto.HistoryOrdersDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdersMapper {

    void insert(Orders order);

    List<OrderVO> getByUserID(HistoryOrdersDTO historyOrdersDTO);

    OrderVO getByID(Long id);

    @Select("select * from orders where number = #{outTradeNo} and user_id= #{userId}")
    Orders getByNumberAndUserId(String outTradeNo, Long userId);

    void update(Orders orders);
}
