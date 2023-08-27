package com.sky.controller.user;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        Long delete = redisTemplate.delete(keys);
        log.info("清理redis缓存成功,本次清理了："+delete+"条数据");
    }

    @PostMapping("/add")
    public Result save(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.save(shoppingCartDTO);
        cleanCache(UserConstant.REDIS_USER_KEY+BaseContext.getCurrentId());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shoppingCartService.list());
    }
}
