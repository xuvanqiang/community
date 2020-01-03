package com.honghuang.community.dao;

import com.honghuang.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     *
     * @param注解用于给参数取名
     *如果只有一个参数,并且会用到映射xml文件的<if></if>标签中,则必须要取别名,否则报错
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 发布帖子
     *
     */
    int insertDiscussPost(DiscussPost post);

    /**
     * 根据帖子id查询帖子详情
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子品论数量
     */
    int updateCommentCount(int id,int commentCount);

    /**
     * 更改帖子类型:用于置顶帖子(0-普通; 1-置顶)
     */
    int updateDiscussPostType(int id,int type);

    /**
     * 更改帖子状态:用于加精或删除帖子(0-正常; 1-精华; 2-删除)
     */
    int updateDiscussPostStatus(int id,int status);
}
