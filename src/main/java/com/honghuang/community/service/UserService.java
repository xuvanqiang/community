package com.honghuang.community.service;

import com.honghuang.community.entity.User;

import java.util.Map;

public interface UserService {
    User findById(int id);

    Map<String,Object> register(User user) throws IllegalAccessException;

    int activation(int userId,String code);
}
