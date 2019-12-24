package com.honghuang.community.controller.interceptor;

import com.honghuang.community.annotation.LoginRequired;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 非法访问拦截器
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){//HandlerMethod是spring提供的一个方法,里面提供了获取方法相关属性的一系列方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired!=null && hostHolder.getUser()==null){ //对请求带有loginRequired标识的方法,但没有登录的情况进行拦截
                response.sendRedirect(request.getContextPath()+"/login"); //把他送回登录页面
                return false;
            }
        }
        return true;
    }
}
