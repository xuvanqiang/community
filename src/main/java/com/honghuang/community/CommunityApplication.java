package com.honghuang.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init() {
        //解决netty启动冲突问题(redis先使用netty,而Elasticsearch就不对netty产生依赖导致无法启用)
        //开关在:org.elasticsearch.transport.netty4.Netty4Utils.setAvailableProcessors
        System.setProperty("es.set.netty.runtime.available.processors","false");//关闭available检测
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
