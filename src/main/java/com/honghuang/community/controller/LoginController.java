package com.honghuang.community.controller;

import com.honghuang.community.entity.User;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

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

}
