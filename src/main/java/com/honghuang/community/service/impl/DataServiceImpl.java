package com.honghuang.community.service.impl;

import com.honghuang.community.service.DataService;
import com.honghuang.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    //定义一个时间格式,方便方法调用
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定的ip计入uv(通过HyperLogLog数据类型)
    public void recordUv(String ip){
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计区间的uv
    public long calculateUv(Date start,Date end){
        if (start==null || end == null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        //整理该日期范围内的key
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){// 终止日期之前进入循环
            String key = RedisKeyUtil.getUvKey(df.format(calendar.getTime()));
            list.add(key);
            calendar.add(Calendar.DATE,1);
        }

        //合并这些数据
        String redisKey = RedisKeyUtil.getUvKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,list);

        //返回统计结果数
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用于计入活跃用户
    public void recordDau(int userId){
        String redisKey = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定日期范围内的活跃用户
    public long calculateDau(Date start,Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //整理该日期范围内的key
        List<byte[]> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){// 终止日期之前进入循环
            String key = RedisKeyUtil.getDauKey(df.format(calendar.getTime()));
            list.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            //合并key
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDauKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),list.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }

}
