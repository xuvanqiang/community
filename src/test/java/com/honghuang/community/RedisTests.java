package com.honghuang.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    //测试string类型数据
    @Test
    public void test1() {
        String redisKey = "java:string";
        //添加string键值对
        redisTemplate.opsForValue().set(redisKey,1);

        //返回键值对
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));//++
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));//--
    }

    //测试hash类数据
    @Test
    public void test2() {
        String redisKey = "java:hash";
        //添加hash键值对
        redisTemplate.opsForHash().put(redisKey,"name","honghuang");
        redisTemplate.opsForHash().put(redisKey,"age","100");

        //返回键值对
        System.out.println(redisTemplate.opsForHash().get(redisKey,"name"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"age"));

    }

    //测试hash类数据
    @Test
    public void test3() {
        String redisKey = "java:list";
        //添加list键值对(左添加)
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));//列表值个数
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));//103:索引0的值
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));//范围0~2索引的值

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//103(左弹出)
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//102(左弹出)
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//101(左弹出)
        redisTemplate.opsForList().leftPush(redisKey, 101);
    }

    //测试set
    @Test
    public void test4() {
        String redisKey = "java:set";

        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));//个数
        System.out.println(redisTemplate.opsForSet().pop(redisKey));//随机弹出一个
        System.out.println(redisTemplate.opsForSet().members(redisKey));//当前集合值的成员
    }

    //测试ZSet
    @Test
    public void test5() {
        String redisKey = "java:ZSet";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);

        //默认排序为分数从小到大,reverse则从大到小
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//值个数
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));//查分数
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));//逆序查排名
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));//逆序取索引0~2的值
    }

    //键操作
    @Test
    public void testKeys() {
        redisTemplate.delete("java:hash");//删除键java:hash

        System.out.println(redisTemplate.hasKey("java:hash"));

        redisTemplate.expire("java:string", 10, TimeUnit.SECONDS);//10秒后删除java:string
    }

    // 批量发送命令,节约网络开销.
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    //redis事务在提交之前会所有对redis操作命令堆积在一起,直到提交时统一发送所有操作给redis服务器执行
    //所以查询操作只能在redis事务执行前,或执行后进行,否则该查询命令不工作
    @Test
    public void testTransaction() {
        Object result = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";

                // 启用事务
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");

                System.out.println(redisOperations.opsForSet().members(redisKey));//print: []

                // 提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(result);//print: [1, 1, 1, [lisi, wangwu, zhangsan]]
    }
}
