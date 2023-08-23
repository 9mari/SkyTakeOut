package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;


public interface DishService {
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    void save(DishDTO dishDTO);
}
