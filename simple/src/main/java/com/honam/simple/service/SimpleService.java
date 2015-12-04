package com.honam.simple.service;

import com.honam.api.annotation.APIMethod;
import com.honam.api.vo.ApiAuthor;
import com.honam.api.vo.ApiCategory;
import com.honam.simple.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xuhaonan on 15/11/26.
 */

@Controller
@RequestMapping("simple")
public class SimpleService {

    @RequestMapping("hello")
    @ResponseBody
    public String hello(String name){
        return name + ",hello";
    }

    @APIMethod(desc = "获取用户信息", category = ApiCategory.USER, apiAuthors = ApiAuthor.Honam_xu, version = "1.0")
    @RequestMapping("getuser")
    @ResponseBody
    public User getUser(String name){
        User u = new User();
        u.setId(0L);
        u.setUserName(name);
        u.setPassword("123");
        return u;
    }
}
