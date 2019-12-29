package com.honghuang.community.service;

import com.honghuang.community.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findComments(int entityType,int entityId,int offset,int limit);

    int findCommentCount(int entityType,int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);
}
