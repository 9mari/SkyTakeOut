package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    List<SetmealDish> getBySetMealId(Long id);

    void insert(SetmealDish setmealDish);

    void deleteBySetMealID(Long id);

    void deleteBySetMealIDs(List<Integer> ids);

    List<Long> getDishID(Long id);
}
