package com.honghuang.community.service;

import com.honghuang.community.entity.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {
    void saveDiscussPost(DiscussPost discussPost);
    void deleteDiscussPost(int id);
    Page<DiscussPost> selectDiscussPost(String keyword, int current, int limit);
}
