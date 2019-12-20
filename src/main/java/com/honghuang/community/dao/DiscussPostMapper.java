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
}
