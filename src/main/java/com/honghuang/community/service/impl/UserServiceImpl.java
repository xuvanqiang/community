package com.honghuang.community.service.impl;

import com.honghuang.community.dao.UserMapper;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(int id) {
        return userMapper.selectById(id);
    }
}
