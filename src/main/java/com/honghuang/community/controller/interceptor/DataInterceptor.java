package com.honghuang.community.controller.interceptor;

import com.honghuang.community.entity.User;
import com.honghuang.community.service.DataService;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //用于统计uv
        String ip = request.getRemoteHost();
        dataService.recordUv(ip);

        //用于统计dau
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDau(user.getId());
        }


        return true;
    }
}
