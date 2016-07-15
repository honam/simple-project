package com.honam.simple.service;

import com.honam.simple.dao.UserDao;
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
}
