package com.honghuang.community.dao.elasticsearch;

import com.honghuang.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ElasticsearchRepository<DiscussPost,Integer>
 *     DiscussPost:要处理的实体类
 *     Integer:实体类中的主键
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
