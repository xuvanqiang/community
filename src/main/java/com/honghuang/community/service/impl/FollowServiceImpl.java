package com.honghuang.community.service.impl;

import com.honghuang.community.entity.User;
import com.honghuang.community.service.FollowService;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import com.honghuang.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService , CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    //关注某实体(用redis的ZSet进行存储,key:followeeKey/followerKey;value:entityId/userId;score:时间毫秒数
    public void follow(int userId,int entityType,int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    //取消关注某实体(移除ZSet的value)
    public void unfollow(int userId,int entityType,int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);

                return redisOperations.exec();
            }
        });
    }

    //查询用户关注的实体数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否已关注实体(查询当前实体对应用户的分数是否存在:比遍历实体速度要快所以用ZSet存储)
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }


    //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        //reversRange()方法的后面两个参数为索引值所以范围为offset ~ (offset + limit - 1)
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(
                followeeKey, offset, offset + limit - 1);
        //无关注直接返回null
        if (targetIds == null) {
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            //关注的用户
            User user = userService.findById(targetId);
            map.put("user",user);
            //关注时间
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }


    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey( ENTITY_TYPE_USER, userId);
        //reversRange()方法的后面两个参数为索引值所以范围为offset ~ (offset + limit - 1)
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(
                followerKey, offset, offset + limit - 1);
        //无关注直接返回null
        if (targetIds == null) {
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId  :  targetIds) {
            Map<String, Object> map = new HashMap<>();
            //被关注的用户
            User user = userService.findById(targetId);
            map.put("user",user);
            //关注时间
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }

}
