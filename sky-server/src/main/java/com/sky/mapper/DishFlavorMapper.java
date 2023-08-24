package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void deleteByDishIds(List<Long> ids);

    void insert(DishFlavor flavor);

    void insertBatch(List<DishFlavor> flavors);

    List<DishFlavor> getByDishId(Integer id);

    void deleteByDishId(Long id);
}
