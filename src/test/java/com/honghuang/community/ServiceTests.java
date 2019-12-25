package com.honghuang.community;

import com.honghuang.community.service.CommentService;
import com.honghuang.community.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ServiceTests {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void test1() {
        System.out.println("查询帖子id为233的1-10评论:"+commentService.findComments(1,233,0,10));
        System.out.println("查询帖子id为233的评论总记录数:"+commentService.findCommentCount(1,233));

    }

    @Test
    public void test2() {
        System.out.println("查询帖子id为233的评论总记录数:"+discussPostService.findDiscussPostRows(0));

    }

}
