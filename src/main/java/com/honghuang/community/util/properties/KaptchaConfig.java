package com.honghuang.community.util.properties;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer KaptchaProducer(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");    //验证码图片宽度
        properties.setProperty("kaptcha.image.height","40");    //验证码图片长度
        properties.setProperty("kaptcha.textproducer.font.size","32");  //验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.color","black");  //验证码字体颜色
        properties.setProperty("kaptcha.textproducer.char.string","0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm");    //验证码内容
        properties.setProperty("kaptcha.textproducer.char.length","4"); //验证码内容长度
//        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");    //干扰类


        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
