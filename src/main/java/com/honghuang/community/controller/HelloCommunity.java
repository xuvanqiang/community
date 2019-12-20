package com.honghuang.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class HelloCommunity {

    @RequestMapping("/index")
    @ResponseBody
    public String hello(){
        return "hello community";
    }
}
