package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/admin/order")
@RestController("adminOrdersController")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> selectOrders(OrdersPageQueryDTO dto) {
        log.info("/conditionSearch分页查询");
        PageResult pageResult = ordersService.selectOrders(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> selectStatus() {
        OrderStatisticsVO vo = ordersService.selectStatus();
        return Result.success(vo);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id){
        OrderVO order = ordersService.getOrder(id);
        return Result.success(order);
    }
}
