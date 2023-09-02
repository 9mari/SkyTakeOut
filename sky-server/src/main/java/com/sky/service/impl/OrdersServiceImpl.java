package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.LBSYunUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO dto) throws Exception {
        Long userID = BaseContext.getCurrentId();
        String key = UserConstant.REDIS_USER_KEY + userID;
        //获取购物车
        List<ShoppingCart> list = (List<ShoppingCart>) redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isEmpty(list)) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //地址薄
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        StringBuilder stringBuilder = new StringBuilder();
        String address = stringBuilder.append(addressBook.getProvinceName()).append(addressBook.getCityName()).append(addressBook.getDistrictName()).append(addressBook.getDetail()).toString();

        String location = LBSYunUtil.parseAddress(address);
        String distance = LBSYunUtil.distance(location);
        if (Integer.valueOf(distance) > 5000) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_TOO_FAR);
        }

        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //订单
        Orders order = new Orders();
        String number = userID + String.valueOf(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(dto, order);
        order.setNumber(number);
        order.setUserId(userID);
        order.setAddress(addressBook.getDetail());
        order.setOrderTime(now);
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        ordersMapper.insert(order);
        Long orderId = order.getId();

        List<OrderDetail> orderDetails = new ArrayList<>();

        //订单详情
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetails.add(orderDetail);
        }

        orderDetailMapper.inserts(orderDetails);
        shoppingCartMapper.delete(BaseContext.getCurrentId());
        redisTemplate.delete(key);
        return OrderSubmitVO.builder().id(orderId).orderNumber(number).orderTime(now).orderAmount(dto.getAmount()).build();
    }

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getByID(userId);
        return new OrderPaymentVO();
    }

    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = ordersMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);
    }

    @Override
    public PageResult selectOrders(OrdersPageQueryDTO dto) {
        PageResult pageResult = new PageResult();
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<OrderVO> orders = ordersMapper.pageQuery(dto);
        if (!CollectionUtils.isEmpty(orders)) {
            for (OrderVO order : orders) {
                order.setOrderDishes(orderDetailName(order));
            }
            Page<OrderVO> list = (Page<OrderVO>) orders;
            pageResult.setTotal(list.getTotal());
            pageResult.setRecords(list.getResult());
        }
        return pageResult;
    }

    @Override
    public OrderStatisticsVO selectStatus() {
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setConfirmed(ordersMapper.selectStatus(Orders.CONFIRMED));
        vo.setToBeConfirmed(ordersMapper.selectStatus(Orders.TO_BE_CONFIRMED));
        vo.setDeliveryInProgress(ordersMapper.selectStatus(Orders.DELIVERY_IN_PROGRESS));
        return vo;
    }

    @Override
    public OrderVO getOrder(Long id) {
        OrderVO vo = ordersMapper.getByID(id);
        vo.setOrderDetailList(orderDetailMapper.getByOrderId(vo.getId()));
//        vo.setOrderDishes(orderDetailName(vo));
        return vo;
    }

    @Override
    public void updateStatus(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        ordersMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCartList = orderDetails.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        OrderVO orderVO = ordersMapper.getByID(ordersRejectionDTO.getId());
        if (Objects.isNull(orderVO)||!(orderVO.getStatus().equals(Orders.TO_BE_CONFIRMED))){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(ordersRejectionDTO.getId()).status(Orders.CANCELLED).rejectionReason(ordersRejectionDTO.getRejectionReason()).build();
        ordersMapper.update(orders);
    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        OrderVO orderVO = ordersMapper.getByID(ordersCancelDTO.getId());
        if (Objects.isNull(orderVO)||orderVO.getStatus().equals(Orders.COMPLETED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(ordersCancelDTO.getId()).status(Orders.CANCELLED).cancelReason(ordersCancelDTO.getCancelReason()).build();
        ordersMapper.update(orders);
    }

    @Override
    public PageResult history(HistoryOrdersDTO historyOrdersDTO) {
        historyOrdersDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(historyOrdersDTO.getPage(), historyOrdersDTO.getPageSize());
        List<OrderVO> list = ordersMapper.getByUserID(historyOrdersDTO);
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.builder().build();
        }
        list.forEach(order -> order.setOrderDetailList(orderDetailMapper.getByOrderId(order.getId())));
        Page<OrderVO> orders = (Page<OrderVO>) list;
        return PageResult.builder().total(orders.getTotal()).records(orders.getResult()).build();
    }

    @Override
    public OrderVO selectById(Long id) {
        OrderVO vo = ordersMapper.getByID(id);
        vo.setOrderDetailList(orderDetailMapper.getByOrderId(vo.getId()));
        return vo;
    }

    @Override
    public void cancel(Long id) {
        OrderVO vo = ordersMapper.getByID(id);
        Integer orderStatus = vo.getStatus();
        if (orderStatus.equals(Orders.PENDING_PAYMENT) || orderStatus.equals(Orders.TO_BE_CONFIRMED)) {
            ordersMapper.update(Orders.builder().id(id).status(Orders.CANCELLED).build());
        }
    }

    private String orderDetailName(OrderVO order) {
        StringBuilder sb = new StringBuilder();
        List<OrderDetail> list = orderDetailMapper.getByOrderId(order.getId());
        List<String> stringList = list.stream().map(orderDetail -> sb.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).toString()).collect(Collectors.toList());
        return String.join(",", stringList);
    }
}