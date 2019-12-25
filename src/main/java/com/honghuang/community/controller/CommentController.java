package com.honghuang.community.controller;

import com.honghuang.community.entity.Comment;
import com.honghuang.community.service.CommentService;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") String discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss_post/detail/" + discussPostId;//重定向到本帖子内
    }
}
