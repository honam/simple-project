package com.honam.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xuhaonan on 15/11/26.
 */

@Controller
@RequestMapping("/simpleService")
public class SimpleService {

    @RequestMapping("hello")
    @ResponseBody
    public String hello(String name){
        return name + ",hello";
    }
}
