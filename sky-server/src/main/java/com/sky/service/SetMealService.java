package com.sky.service;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetMealService {
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);
}
