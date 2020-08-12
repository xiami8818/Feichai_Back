package com.feichai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class Page {
    @RequestMapping(value = {"","/index"},method = RequestMethod.GET)
    public String index(){
        return "html/index";
    }
    @RequestMapping("/login")
    public String login(){
        return "html/login";
    }
}
