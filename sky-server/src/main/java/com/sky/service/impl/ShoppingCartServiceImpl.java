package com.sky.service.impl;

import com.sky.constant.UserConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public void  save(ShoppingCartDTO shoppingCartDTO) {
        //线程获取id
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Boolean presenceFlag = false;

        String key = UserConstant.REDIS_USER_KEY+shoppingCart.getUserId();
        //查询当前用户缓存购物车中是否有此商品
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<ShoppingCart> shoppingCartList = (List<ShoppingCart>) valueOperations.get(key);

        //判断redis获取的是否为空
        if (!CollectionUtils.isEmpty(shoppingCartList)){
            for (ShoppingCart shopping : shoppingCartList) {
                //遍历是否存在传入的商品id
                Long dishId = shopping.getDishId();
                Long dishId1 = shoppingCart.getDishId();
                Long setmealId = shopping.getSetmealId();
                Long setmealId1 = shoppingCart.getSetmealId();

                if ((dishId1 != null && dishId.equals(dishId1))||(setmealId1 != null && setmealId.equals(setmealId1))){
                    shoppingCart.setNumber(shopping.getNumber() + 1);
                    shoppingCart.setId(shopping.getId());
                    //下文统一处理
                    presenceFlag = true;
                }
            }
        }else {
            //为空则通过sql命令查询
            shoppingCartList = shoppingCartMapper.list(shoppingCart);
            if (!CollectionUtils.isEmpty(shoppingCartList)){
                ShoppingCart shopping = shoppingCartList.get(0);
                shoppingCart.setNumber(shopping.getNumber() + 1);
                //下文统一处理
                presenceFlag = true;
            }
        }
        //如果有update对应的商品，没有则insert
        if (presenceFlag){
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //添加到购物车的是菜品
                DishVO dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //添加到购物车的是套餐
                SetmealVO setmeal = setMealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        Long userID = BaseContext.getCurrentId();
        String key = UserConstant.REDIS_USER_KEY+userID;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<ShoppingCart> list = (List<ShoppingCart>)valueOperations.get(key);
        if (!CollectionUtils.isEmpty(list)){
            return list;
        }
        list = shoppingCartMapper.list(ShoppingCart.builder().userId(userID).build());
        valueOperations.set(key,list);
        return list;
    }

    @Override
    public void delete() {
        Long userID = BaseContext.getCurrentId();
        shoppingCartMapper.delete(userID);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
