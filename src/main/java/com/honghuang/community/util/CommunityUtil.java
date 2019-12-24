package com.honghuang.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
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

    //json转换工具
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if (map != null) {
            for (String str : map.keySet()) {
                jsonObject.put(str,map.get(str));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

}
