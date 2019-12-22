package com.honghuang.community.service.impl;

import com.honghuang.community.dao.LoginTicketMapper;
import com.honghuang.community.dao.UserMapper;
import com.honghuang.community.entity.LoginTicket;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import com.honghuang.community.util.CommunityUtil;
import com.honghuang.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        //空值判断
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }

        //验证账号
        User checkUser = userMapper.selectByName(user.getUsername());
        if (checkUser != null) {
            map.put("usernameMsg","该账号已存在!");
            return map;
        }
        //验证邮箱
        checkUser = userMapper.selectByEmail(user.getEmail());
        if (checkUser != null) {
            map.put("emailMsg","邮箱已被注册!");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();    //thymeleaf模板上下文
        context.setVariable("email",user.getEmail());
        //http://localhost:8848/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (code.equals(user.getActivationCode())) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }

    }

    /**
     * 用户登录service层
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();

        //空值判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }

        //验证账号合法性
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg","该用户不存在!");
            return map;
        }

        //验证账号状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg","该用户未激活!");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码不正确!");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);//更改ticket记录的状态

    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket); //通过ticket查询loginTicket对象
    }


}
