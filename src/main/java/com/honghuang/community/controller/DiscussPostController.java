package com.honghuang.community.controller;

import com.honghuang.community.entity.*;
import com.honghuang.community.event.EventProducer;
import com.honghuang.community.service.CommentService;
import com.honghuang.community.service.DiscussPostService;
import com.honghuang.community.service.LikeService;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import com.honghuang.community.util.CommunityUtil;
import com.honghuang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss_post")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    //发布帖子
    @PostMapping("/publish")
    @ResponseBody
    public String publish(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //帖子新增数据:触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "发布成功");
    }


    //查看帖子详情
    @GetMapping("/detail/{discussPostId}")
    public String findDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.findById(post.getUserId());
        model.addAttribute("user", user);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        //点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        //评论分页信息
        page.setRows(post.getCommentCount()); //这里也可以用commentMapper查询count
        page.setPath("/discuss_post/detail/" + discussPostId);
        page.setLimit(5);

        //评论:给帖子的评论
        //回复:给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findComments(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //评论vo
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //作者
                commentVo.put("user", userService.findById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //回复列表
                List<Comment> replyList = commentService.findComments(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);//回复不做分页,全部显示

                //回复vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();

                        //回复
                        replyVo.put("reply", reply);
                        //作者
                        replyVo.put("user", userService.findById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findById(reply.getTargetId());
                        replyVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

    //置顶帖子
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateDiscussType(id,1);

        //帖子变更数据:触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    //置顶帖子
    @PostMapping("/elite")
    @ResponseBody
    public String setElite(int id){
        discussPostService.updateDiscussStatus(id,1);

        //帖子变更数据:触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    //删除帖子
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateDiscussStatus(id,2);

        //帖子变更数据:触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
}
