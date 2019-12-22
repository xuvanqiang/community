package com.honghuang.community.controller;

import com.google.code.kaptcha.Producer;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @GetMapping("/register")
    public String getRegister(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLogin(){
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user) throws IllegalAccessException {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送一封激活邮件,请尽快激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }

    }

    //http://localhost:8848/community/activation/101/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId")int userId,@PathVariable("code")String code){
        int activation = userService.activation(userId, code);
        if (activation == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功,您的账号已经可以正常使用!");
            model.addAttribute("target","/login");
        }else if (activation == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作,该账号已经激活过了!");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败,您提供的激活码不正确!");
            model.addAttribute("target","/index");
        }

        return "site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {   //无需手动释放资源,因为交给spring容器管理了
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("验证码响应失败:"+e.getMessage());
        }

    }

    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberme,
                        Model model,HttpSession session,HttpServletResponse response){//当获取参数不是对象实体时,需要手动装载参数进model中
        //判断验证码正确性
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确!");
            return "/site/login";
        }

        //检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);    //cookie的作用范围
            cookie.setMaxAge(expiredSeconds);   //设置cookie的最大存活时间
            response.addCookie(cookie); //给登录成功的客户端返回带有cookie的响应信息
            return "redirect:/index"; //登录成功重定向到首页
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login"; //登录失败
        }

    }

    @GetMapping("/logout")
    public String Logout(@CookieValue("ticket") String ticket){//该注解可用于直接接收value值对应cookie,比遍历方法更简洁
        userService.logout(ticket);
        return "redirect:/login";
    }
}
