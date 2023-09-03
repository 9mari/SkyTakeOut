package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    User getByOpenID(String openID);

    void insert(User user);

    User getByID(Long userId);


    Integer getByDate(Map map);
}
