package com.honghuang.community.service.impl;

import com.honghuang.community.dao.CommentMapper;
import com.honghuang.community.dao.DiscussPostMapper;
import com.honghuang.community.entity.Comment;
import com.honghuang.community.service.CommentService;
import com.honghuang.community.util.CommunityConstant;
import com.honghuang.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService , CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCommentsCountByEntity(entityType,entityId);
    }

    /**
     *给定一个声明式事务,添加评论时应该和帖子中的评论数量更改一同完成或失败
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        //空值判断
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//转义html格式符号,防止第三方将元素注入页面
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//过滤敏感词
        int rows = commentMapper.insertComment(comment);


        //更新帖子的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentsCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(),count);
        }

        return 0;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
