package com.honghuang.community.util;

import com.honghuang.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.具有线程隔离的功能
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();//用currentThread空间中的map来存储数据

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clean(){
        users.remove();
    }
}
