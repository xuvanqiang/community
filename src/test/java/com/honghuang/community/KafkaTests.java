package com.honghuang.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProduct kafkaProduct;

    @Test
    public void testKafka() {
        kafkaProduct.sendMessage("publish","你好");
        kafkaProduct.sendMessage("publish","我是第二条信息");
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


//消息生产者
@Component
class KafkaProduct{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content) {
        kafkaTemplate.send(topic,content);
    }
}


//消息消费者(自动生效,自动调用listener)
/*@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}*/
