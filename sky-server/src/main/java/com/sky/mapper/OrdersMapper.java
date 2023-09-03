package com.sky.mapper;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.HistoryOrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {

    void insert(Orders order);

    List<OrderVO> getByUserID(HistoryOrdersDTO historyOrdersDTO);

    OrderVO getByID(Long id);

    @Select("select * from orders where number = #{outTradeNo} and user_id= #{userId}")
    Orders getByNumberAndUserId(String outTradeNo, Long userId);

    void update(Orders orders);

    List<OrderVO> pageQuery (OrdersPageQueryDTO dto);

    Integer selectStatus(Integer status);

    List<Orders> getUnpaidOrders();

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    @Select("SELECT SUM(amount) FROM orders WHERE STATUS = #{status} AND order_time BETWEEN #{begin} AND #{end}")
    Double getByDate(Map map);
}
