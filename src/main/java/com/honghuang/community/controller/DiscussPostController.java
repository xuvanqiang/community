package com.honghuang.community.controller;

import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.DiscussPostService;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityUtil;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/discuss_post")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //发布帖子
    @PostMapping("/publish")
    @ResponseBody
    public String publish(String title,String content){
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403,"您还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0,"发布成功");
    }


    //查看帖子详情
    @GetMapping("/detail/{discussPostId}")
    public String findDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findById(post.getUserId());
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }
}
