package com.honghuang.community.service;

import com.honghuang.community.entity.LoginTicket;
import com.honghuang.community.entity.User;

import java.util.Map;

public interface UserService {
    User findById(int id);

    Map<String,Object> register(User user) throws IllegalAccessException;

    int activation(int userId,String code);

    Map<String,Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeaderUrl(int userId,String headerUrd);
}
