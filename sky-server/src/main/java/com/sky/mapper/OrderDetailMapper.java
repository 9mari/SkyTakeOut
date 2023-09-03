package com.sky.mapper;


import com.sky.dto.DateDTO;
import com.sky.entity.OrderDetail;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.SalesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {
    void inserts(List<OrderDetail> orderDetails);

    List<OrderDetail> getByOrderId(Long OrderId);

    @Select("SELECT name,SUM(od.number) AS numbers FROM order_detail od JOIN orders o ON order_id = o.id WHERE order_time BETWEEN #{begin} AND #{end} GROUP BY name ORDER BY numbers DESC LIMIT 10;")
    List<SalesVO> getSalesTop10(DateDTO dto);
}
