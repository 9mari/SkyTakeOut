package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
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
    public Result<PageResult> searchOrders(OrdersPageQueryDTO dto) {
        log.info("/conditionSearch分页查询");
        PageResult pageResult = ordersService.searchOrders(dto);
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

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        ordersService.updateStatus(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        ordersService.updateStatus(OrdersConfirmDTO.builder().id(id).status(Orders.DELIVERY_IN_PROGRESS).build());
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        ordersService.updateStatus(OrdersConfirmDTO.builder().id(id).status(Orders.COMPLETED).build());
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        ordersService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result adminCancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        ordersService.adminCancel(ordersCancelDTO);
        return Result.success();
    }
}
