package com.honam.simple.service;

import com.honam.base.api.annotation.APIMethod;
import com.honam.base.api.annotation.APIParam;
import com.honam.base.api.vo.ApiAuthor;
import com.honam.base.api.vo.ApiCategory;
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

    @APIMethod(desc = "获取用户信息", category = ApiCategory.USER, apiAuthors = ApiAuthor.Honam_xu, version = "1.0")
    @RequestMapping("getuser")
    @ResponseBody
    public User getUser(@APIParam(isRequired = true, description = "用户名") String name){
        User u = new User();
        u.setId(0L);
        u.setUserName(name);
        u.setPassword("123");
        return u;
    }
}
