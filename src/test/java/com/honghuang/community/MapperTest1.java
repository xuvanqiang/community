package com.honghuang.community;

import com.honghuang.community.dao.*;
import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.entity.LoginTicket;
import com.honghuang.community.entity.Message;
import com.honghuang.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest1 {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(111);
        System.out.println(user);

        user = userMapper.selectByName("aaa");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder111@sina.com");
        System.out.println(user);
    }

    @Test
    public void testUser2(){
        User user = new User();
        user.setUsername("testuser1");
        user.setPassword("abc123");
        user.setSalt("abc");
        user.setEmail("sun123@163.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/677t.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }


    @Test
    public void testUser3(){
        int row = 0;
        User user = userMapper.selectById(150);
        System.out.println("变更行数:"+row+",信息:"+user);
        row = userMapper.updateHeader(user.getId(), "http://images.nowcoder.com/head/244t.png");
        System.out.println("变更行数:"+row+",信息:"+userMapper.selectById(150));
        row = userMapper.updateStatus(user.getId(), 0);
        System.out.println("变更行数:"+row+",信息:"+userMapper.selectById(150));
        row = userMapper.updatePassword(user.getId(), "sda434556");
        System.out.println("变更行数:"+row+",信息:"+userMapper.selectById(150));
    }

    /**
     * 测试discussPostMapper的可用性
     */
    @Test
    public void test1() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
//        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    /**
     * 测试loginTicket添加
     */
    @Test
    public void testLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    /**
     * 测试loginTicket查询更改
     */
    @Test
    public void testLoginTicket2(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus(loginTicket.getTicket(),1);
        System.out.println(loginTicket);
    }

    /**
     * 测试一下commentMapper的可用性
     */
    @Test
    public void testCommentMapper() {
        System.out.println("查询帖子id为233的1-10评论:"+commentMapper.selectCommentsByEntity(1,233,0,10));
        System.out.println("查询帖子id为233的评论总记录数:"+commentMapper.selectCommentsCountByEntity(1,233));
    }

    @Test
    public void testSelectLetters() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }

}
