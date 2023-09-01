package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.HistoryOrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.LBSYunUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        String key = UserConstant.REDIS_USER_KEY+userID;
        //获取购物车
        List<ShoppingCart> list = (List<ShoppingCart>)redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isEmpty(list)){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //地址薄
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        StringBuilder stringBuilder = new StringBuilder();
        String address = stringBuilder.append(addressBook.getProvinceName()).append(addressBook.getCityName()).append(addressBook.getDistrictName()).append(addressBook.getDetail()).toString();

        String location = LBSYunUtil.parseAddress(address);
        String distance = LBSYunUtil.distance(location);
        if (Integer.valueOf(distance) > 5000){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_TOO_FAR);
        }

        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //订单
        Orders order = new Orders();
        String number =userID + String.valueOf(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(dto,order);
        order.setNumber(number);
        order.setUserId(userID);
        order.setAddress(addressBook.getDetail());
        order.setOrderTime(now);
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setStatus(Orders.PENDING_PAYMENT );
        order.setPayStatus(Orders.UN_PAID);
        ordersMapper.insert(order);
        Long orderId = order.getId();

        List<OrderDetail> orderDetails = new ArrayList<>();

        //订单详情
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetails.add(orderDetail);
        }

        orderDetailMapper.inserts(orderDetails);
        shoppingCartMapper.delete(BaseContext.getCurrentId());
        return OrderSubmitVO.builder().id(orderId).orderNumber(number).orderTime(now).orderAmount(dto.getAmount()).build();
    }

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getByID(userId);
        return new OrderPaymentVO();
    }

    @Override
    public PageResult history(HistoryOrdersDTO historyOrdersDTO) {
        historyOrdersDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(historyOrdersDTO.getPage(),historyOrdersDTO.getPageSize());
        List<OrderVO> list = ordersMapper.getByUserID(historyOrdersDTO);
        list.forEach(order -> order.setOrderDetailList(orderDetailMapper.getByOrderId(order.getId())));
        Page<OrderVO> orders = (Page<OrderVO>) list;
        return PageResult.builder().total(orders.getTotal()).records(orders.getResult()).build();
    }
}