package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.service.OrdersService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        Long userID = BaseContext.getCurrentId();
        String key = UserConstant.REDIS_USER_KEY+userID;
        //获取购物车
        List<ShoppingCart> list = (List<ShoppingCart>)redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isEmpty(list)){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //地址薄
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
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

        return OrderSubmitVO.builder().id(orderId).orderNumber(number).orderTime(now).orderAmount(dto.getAmount()).build();
    }
}