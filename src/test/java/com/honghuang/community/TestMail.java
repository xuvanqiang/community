package com.honghuang.community;

import com.honghuang.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMail {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail() {
        mailClient.sendMail("1297386632@qq.com","不要报错了","最后的大招：既然邮箱B能发给自己，那就发给自己然后抄送给别人,上午在服务器测试还是不行，结果下午再测试的时候就能发送成功了，163太恶心");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username","xiaoxiami");
        String content = templateEngine.process("/mail/mail-html-test", context);
        System.out.println(content);
        mailClient.sendMail("1297386632@qq.com","HTML",content);
    }

    @Test
    public void testTime() {
        System.out.println(new Date(System.currentTimeMillis() + 24*30*3600*1000L));
    }
    
}
