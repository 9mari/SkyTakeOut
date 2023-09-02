package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrdersTask {

    @Autowired
    private OrdersMapper ordersMapper;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void processTimeoutOrder(){
        log.info("检查是否有订单超时");
        LocalDateTime overTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> unpaidOrders = ordersMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT,overTime);
        if (!CollectionUtils.isEmpty(unpaidOrders)){
            unpaidOrders.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                ordersMapper.update(order);
            });
        }
//        for (Orders order : unpaidOrders) {
//            if (Duration.between(order.getOrderTime(), LocalDateTime.now()).toMinutes() > 15){
//                order.setStatus(Orders.CANCELLED);
//                order.setCancelReason("支付超时，自动取消");
//                order.setCancelTime(LocalDateTime.now());
//                ordersMapper.update(order);
//            }
//        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("检测是否1点仍有订单在派送中");
        LocalDateTime overTime = LocalDateTime.now().plusHours(-1);
        List<Orders> list = ordersMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, overTime);
        if (!CollectionUtils.isEmpty(list)){
            list.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                ordersMapper.update(order);
            });
        }
    }
}
