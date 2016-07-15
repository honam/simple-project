package com.honam.simple.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.honam.simple.dao.UserDao;
import com.honam.simple.dao.UserMapper;
import com.honam.simple.imterface.Hello;
import com.honam.simple.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Honam on 2016/7/15.
 */
@Service
public class HelloImpl implements Hello {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserMapper userMapper;
    @Override
    public String sayHello() {
        return "Hello.";
    }

    @Override
    public List<User> getUser() {
        List<User> userVos = new ArrayList<>();
        List<com.honam.simple.models.User> users = userDao.getUser();
        for(com.honam.simple.models.User u : users){
            User user = new User();
            user.setId(u.getId());
            user.setUserName(u.getName());
            userVos.add(user);
        }
        return userVos;
    }

    @Override
    public Page<User> getUserByPage() {
        List<User> userVos = new ArrayList<>();
        PageHelper.startPage(1,2);
        Page<com.honam.simple.models.User> userList = (Page<com.honam.simple.models.User>)userMapper.getUserList();
        for(com.honam.simple.models.User u : userList){
            User user = new User();
            user.setId(u.getId());
            user.setUserName(u.getName());
            userVos.add(user);
        }
        Page<User> result = new Page<User>();
        result.addAll(userVos);
        result.setPageSize(userList.getPageSize());
        result.setPageNum(userList.getPageNum());
        result.setTotal(userList.getTotal());
        return result;
    }
}
