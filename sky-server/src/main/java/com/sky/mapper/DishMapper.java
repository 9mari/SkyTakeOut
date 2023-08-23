package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFillAnnotation;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    List<Dish> getByIds(List<Long> ids);


    @AutoFillAnnotation(value = OperationType.INSERT)
    void insert(DishDTO dishDTO);
}