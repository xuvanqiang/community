package com.honghuang.community.service;

public interface LikeService {
    void like(int userId,int entityUserId,int entityType,int entityId);

    long findEntityLikeCount(int entityType, int entityId);

    int findEntityLikeStatus(int userId,int entityType,int entityId);

    long findUserLikeCount(int userId);
}
