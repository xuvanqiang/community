package com.honghuang.community.controller.interceptor;

import com.honghuang.community.entity.LoginTicket;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CookieUtil;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截器,批量处理请求
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 该方法执行在controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效(即判断loginTicket是否非空, Status是否为0, Expired是否在当前时刻之后 )
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据凭证查询用户
                User user = userService.findById(loginTicket.getUserId());
                //在本次请求有持有用户
                hostHolder.setUser(user);

                //构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }

        return true;
    }

    /**
     * 该方法执行在controller之后,模板之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser",user);
        }
    }

    /**
     * 该方法执行在模板之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clean();
        //一并清除security的context
        SecurityContextHolder.clearContext();
    }
}
