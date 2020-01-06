package com.honghuang.community.controller.advice;


import com.honghuang.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * controller异常处理类,统一处理所有异常
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {//这三参数可以解决大多数问题
        //记录错误日志,方便维护
        logger.error("服务器发生异常:"+e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        /**
         * 通过请求头获取应该给客户端返回的内容:异步或同步请求(即返回json或页面)
         */
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));
        }else {
            response.sendRedirect(request.getContextPath() + "/errorbat");
        }

    }


}
