package com.honghuang.community.controller;

import com.honghuang.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //统计页面
    @GetMapping("/data")
    public String getDataPage(){
        return "/site/admin/data";
    }

    /**统计网站uv
     * @DateTimeFormat:用于接收前端日期型数据
     *返回统计数和需要回显的日期数据
     */
    @PostMapping("/data/uv")
    public String getUv(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long uv = dataService.calculateUv(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        return "/site/admin/data";
    }

    @PostMapping("/data/dau")
    public String getDau(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long dau = dataService.calculateDau(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        return "/site/admin/data";
    }
}
