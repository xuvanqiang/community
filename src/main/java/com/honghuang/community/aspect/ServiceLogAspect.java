package com.honghuang.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * aop日志记录
 */
@Component
@Aspect
public class ServiceLogAspect {

    //先定义一个logger
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //配置切点
    @Pointcut("execution(* com.honghuang.community.service.*.*(..))")
    public void pointcut() {
        
    }

    //切面
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        //eg: 用户[127.0.0.1],在[yyyy-MM-dd HH-mm-ss],访问了[com.honghuang.community.service.xx()].
        //从spring提供的request上下文中提取出attributes(这里强转成其子类:因为功能多一些)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //通过attributes获取request内容
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        //通过request获取ip
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        //方法可以通过JoinPoint对象获取
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }

}
