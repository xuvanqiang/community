package com.honghuang.community.controller;

import com.honghuang.community.entity.DiscussPost;
import com.honghuang.community.entity.Page;
import com.honghuang.community.service.ElasticsearchService;
import com.honghuang.community.service.LikeService;
import com.honghuang.community.service.UserService;
import com.honghuang.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //要回显搜索结果到地址栏,选用get方法请求;格式:search?keyword = xxx
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //搜索帖子 spring的Page中的current是从0开始的所以减1
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.selectDiscussPost(keyword,page.getCurrent()-1,page.getLimit());
        //聚合数据
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                HashMap<String, Object> map = new HashMap<>();
                //帖子
                map.put("post",post);
                //作者
                map.put("user",userService.findById(post.getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        //关键词
        model.addAttribute("keyword",keyword);

        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }
}
