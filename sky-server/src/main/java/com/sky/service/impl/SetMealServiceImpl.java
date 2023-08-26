package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<SetmealVO> setmealVOS = setMealMapper.page(setmealPageQueryDTO);
        for (SetmealVO setmealVO : setmealVOS) {
            List<SetmealDish> setMealDishes = setMealDishMapper.getBySetMealId(setmealVO.getId());
            if (!CollectionUtils.isEmpty(setMealDishes)) {
                setmealVO.setSetmealDishes(setMealDishes);
            }
        }
        Page<SetmealVO> list = (Page<SetmealVO>) setmealVOS;
        PageResult pageResult = new PageResult(list.getTotal(), list.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.insert(setmeal);
        //取出传入的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //菜品非空校验
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
            for (SetmealDish setmealDish : setmealDishes) {
                setMealDishMapper.insert(setmealDish);
            }
        }
    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setMealMapper.getById(id);
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetMealId(id);
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            setmealVO.setSetmealDishes(setmealDishes);
        }
        return setmealVO;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);
        setMealDishMapper.deleteBySetMealID(setmealDTO.getId());
        //取出传入的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //菜品非空校验
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
            for (SetmealDish setmealDish : setmealDishes) {
                setMealDishMapper.insert(setmealDish);
            }
        }
    }

    @Override
    public void enOrDis(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        List<Long> ids = setMealDishMapper.getDishID(id);
        List<Dish> dishes = dishMapper.getByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus().equals(StatusConstant.DISABLE)){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        setMealMapper.update(setmeal);
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        List<Setmeal> setMeals = setMealMapper.getByIds(ids);
        for (Setmeal setMeal : setMeals) {
            if (setMeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setMealDishMapper.deleteBySetMealIDs(ids);
        setMealMapper.delete(ids);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }


}
