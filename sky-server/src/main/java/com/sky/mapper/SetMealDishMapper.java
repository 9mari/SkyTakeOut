package com.sky.mapper;

import com.sky.entity.SetmealDish;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    List<SetmealDish> getBySetMealId(Long id);
}
