package com.feichai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Page {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){ return "Hello World";}
}
