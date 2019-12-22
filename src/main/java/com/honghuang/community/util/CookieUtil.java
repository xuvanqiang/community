package com.honghuang.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * cookie工具类
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name){
        //空值判断
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }

        //通过request得到cookie数组,再进行遍历找到和name相匹配的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}