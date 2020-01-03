package com.honghuang.community.config;

import com.honghuang.community.quartz.ExampleJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置 -->数据库 -->调用
@Configuration
public class QuartzConfig {

    /*
    FactoryBean可以简化Bean的实例化过程:
    1.通过FactoryBean封装Bean的实例化过程.
    2.将FactoryBean装配到Spring容器里.
    3.将FactoryBean注入给其他的Bean.
    4.该Bean得到的是FactoryBean所管理的对象实例.
     */

    //配置JobDetail
//    @Bean
    public JobDetailFactoryBean exampleJobDetail(){
        JobDetailFactoryBean bean = new JobDetailFactoryBean();
        bean.setJobClass(ExampleJob.class);//事件
        bean.setName("exampleJob");
        bean.setGroup("exampleJobGroup");
        bean.setDurability(true);//任务是否持久保存
        bean.setRequestsRecovery(true);//任务是否可恢复
        return bean;
    }

    //配置Trigger(SimpleTriggerFactoryBean/CronTriggerFactoryBean)
    //CronTriggerFactoryBean比SimpleTriggerFactoryBean可以触发更复杂的时间点
//    @Bean
    public SimpleTriggerFactoryBean exampleTrigger(JobDetail exampleJobDetail){
        SimpleTriggerFactoryBean bean = new SimpleTriggerFactoryBean();
        bean.setJobDetail(exampleJobDetail);
        bean.setName("exampleTrigger");
        bean.setGroup("exampleTriggerGroup");
        bean.setRepeatInterval(3000);//时间间隔
        bean.setJobDataMap(new JobDataMap());
        return bean;
    }
}
