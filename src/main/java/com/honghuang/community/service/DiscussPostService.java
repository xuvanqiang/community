package com.honghuang.community.service;

import com.honghuang.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost post);

    DiscussPost findDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateDiscussType(int id,int type);

    int updateDiscussStatus(int id,int status);

    int updateScore(int id , double score);

}
