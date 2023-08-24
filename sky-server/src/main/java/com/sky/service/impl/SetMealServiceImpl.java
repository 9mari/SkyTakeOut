package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        List<SetmealVO> setmealVOS = setMealMapper.page(setmealPageQueryDTO);
        for (SetmealVO setmealVO : setmealVOS) {
            List<SetmealDish> setMealDishes = setMealDishMapper.getBySetMealId(setmealVO.getId());
            if (setMealDishes != null && !setMealDishes.isEmpty()){
                setmealVO.setSetmealDishes(setMealDishes);
            }
        }
        Page<SetmealVO> list = (Page<SetmealVO>) setmealVOS;
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());
        return pageResult;
    }
}
