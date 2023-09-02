package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrdersService {
    OrderSubmitVO submit(OrdersSubmitDTO dto) throws Exception;

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult history(HistoryOrdersDTO historyOrdersDTO);

    OrderVO selectById(Long id);

    void cancel(Long id);

    void paySuccess(String outTradeNo);

    PageResult selectOrders(OrdersPageQueryDTO dto);

    OrderStatisticsVO selectStatus();

    OrderVO getOrder(Long id);

    void updateStatus(OrdersConfirmDTO ordersConfirmDTO);

    void repetition(Long id);

    void rejection(OrdersRejectionDTO ordersCancelDTO);

    void adminCancel(OrdersCancelDTO ordersCancelDTO);
}
