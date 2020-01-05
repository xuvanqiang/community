package com.honghuang.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.honghuang.community.dao.DiscussPostMapper;
import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.service.DiscussPostService;
import com.honghuang.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.post.max-size}")
    private int maxSize;

    @Value("${caffeine.post.expire-seconds}")
    private int expiredSeconds;

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postCountCache;
    
    @PostConstruct
    public void init() {
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expiredSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String s) throws Exception {
                        if (s == null || s.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = s.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        logger.info("从数据库读取帖子列表数据");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postCountCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expiredSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer integer) throws Exception {
                        logger.info("从数据库读取帖子总数数据");
                        return discussPostMapper.selectDiscussPostRows(integer);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
        //访问首页热门帖子时从缓存中读取
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset+ ":" + limit);
        }

        logger.info("从数据库读取帖子列表数据");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postCountCache.get(userId);
        }

        logger.info("从数据库读取帖子总数数据");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        //空值判断
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //对html内容进行转义,使标题和内容变成string(用springmvc提供的工具类htmlUtil就可以轻松解决)
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //对内容进行敏感词过滤
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }


    public int updateDiscussType(int id,int type){
        return discussPostMapper.updateDiscussPostType(id,type);
    }

    public int updateDiscussStatus(int id,int status){
        return discussPostMapper.updateDiscussPostStatus(id,status);
    }

    public int updateScore(int id , double score){
        return discussPostMapper.updateScore(id,score);
    }

}
