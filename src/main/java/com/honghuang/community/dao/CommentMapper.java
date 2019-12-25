package com.honghuang.community.dao;

import com.honghuang.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 分页查询评论
     */
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    /**
     * 查询评论数
     */
    int selectCommentsCountByEntity(int entityType,int entityId);

    /**
     * 添加评论
     */
    int insertComment(Comment comment);
}
