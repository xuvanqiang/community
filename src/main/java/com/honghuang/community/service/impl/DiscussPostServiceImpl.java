package com.honghuang.community.service.impl;

import com.honghuang.community.dao.DiscussPostMapper;
import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.service.DiscussPostService;
import com.honghuang.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
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

}
