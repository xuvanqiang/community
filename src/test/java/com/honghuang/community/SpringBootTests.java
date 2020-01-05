package com.honghuang.community;

import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * springboot的junit4测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    /**
     * 类加载之前run
     */
    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    /**
     * 类执行结束之前run
     */
    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    //每个Test之前执行,用于生产测试数据
    @Before
    public void before() {
        System.out.println("before");

        // 初始化测试数据
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    //每个Test之后执行,用于销毁测试数据
    @After
    public void after() {
        System.out.println("after");

        // 删除测试数据
        discussPostService.updateDiscussStatus(data.getId(), 2);
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
    }

}
