package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrdersService {
    OrderSubmitVO submit(OrdersSubmitDTO dto) throws Exception;
}
