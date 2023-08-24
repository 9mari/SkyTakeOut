package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> list = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        List<Dish> dishes = dishMapper.getByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> list = setMealDishMapper.getSetMealByDishIds(ids);
        if (!CollectionUtils.isEmpty(list)){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.delete(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!CollectionUtils.isEmpty(flavors)){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public DishVO getById(Integer id) {
        DishVO dishVO = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        if (!CollectionUtils.isEmpty(flavors)){
            dishVO.setFlavors(flavors);
        }
        return dishVO;
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dish.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!CollectionUtils.isEmpty(flavors)){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void enOrDis(Integer status, Long id) {
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.update(dish);
    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }
}
