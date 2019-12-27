package com.honghuang.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 改变template的序列化方式,把RedisTemplate配置成string,obj类型方便使用
     * @param factory 连接工厂,用于连接redis
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式(这里序列化成json格式,方便解释使用)
        template.setValueSerializer(RedisSerializer.json());
        //hash格式比较特殊,需要对其key,value也进行一轮序列化
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //让序列化配置生效
        template.afterPropertiesSet();

        return template;
    }
}
