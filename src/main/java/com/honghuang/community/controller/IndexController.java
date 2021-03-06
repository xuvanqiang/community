package com.honghuang.community.controller;

import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.entity.Page;
import com.honghuang.community.entity.User;
import com.honghuang.community.service.DiscussPostService;
import com.honghuang.community.service.LikeService;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String getIndex(Model model, Page page){
        return "forward:/index";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0")int orderMode ){
         /*方法调用之前,SpringMVC会自动实例化Model和Page,并将Page注入到Model
         所以,在thymeleaf可以直接访问Page对象中的数据*/
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<DiscussPost> list = discussPostService.findDiscussPosts(
                0, page.getOffset(), page.getLimit(),orderMode);
        ArrayList<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findById(post.getUserId());
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/errorbat/500";
    }

    @GetMapping(path = "/denied")
    public String getDeniedPage() {
        return "/errorbat/404";
    }

}
