package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getByOpenID(String openID);

    void insert(User user);

    User getByID(Long userId);
}
