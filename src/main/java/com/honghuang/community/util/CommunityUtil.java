package com.honghuang.community.util;

import com.honghuang.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");//将uuid生成的中划线转成空字符,便于使用
    }

    //MD5加密
    //password + salt --> MD5_password(给用户密码加上一个盐(随机的字符串)后再通过MD5加密,避免密码简单被破解)
    public static String md5(String key){
        if (StringUtils.isBlank(key)){//即判断key==null||key==""||key.length()==0
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
