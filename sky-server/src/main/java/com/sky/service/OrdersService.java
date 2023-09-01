package com.sky.service;

import com.sky.dto.HistoryOrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrdersService {
    OrderSubmitVO submit(OrdersSubmitDTO dto) throws Exception;

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult history(HistoryOrdersDTO historyOrdersDTO);
}
