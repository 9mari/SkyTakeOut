package com.sky.controller.user;

import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.HistoryOrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/user/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RedisTemplate redisTemplate;

    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        Long delete = redisTemplate.delete(keys);
        log.info("清理redis缓存成功,本次清理了：" + delete + "条数据");
    }

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO dto) throws Exception {
        OrderSubmitVO vo = ordersService.submit(dto);
        return Result.success(vo);
    }

    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        HashMap<String, String> map = new HashMap<>();
        map.put("number", ordersPaymentDTO.getOrderNumber());
        HttpClientUtil.doGet("http://localhost:8080/notify/paySuccess", map);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> history(HistoryOrdersDTO historyOrdersDTO) {
        PageResult history = ordersService.history(historyOrdersDTO);
        return Result.success(history);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> selectById(@PathVariable Long id) {
        OrderVO vo = ordersService.selectById(id);
        return Result.success(vo);
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        ordersService.cancel(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id) {
        ordersService.repetition(id);
        return Result.success();
    }
}
