package com.honghuang.community;

import com.honghuang.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test() {
        String str = "店铺今天新开张,里面可以搞吸毒,玩赌博,....还有嫖娼";
        str = sensitiveFilter.filter(str);
        System.out.println(str);

        str = "这里可以☆赌☆博☆,可以☆嫖☆娼☆,可以☆吸☆毒☆,可以☆开☆票☆,哈哈哈!";
        str = sensitiveFilter.filter(str);
        System.out.println(str);
    }
}
