package com.honam.simple.imterface;

import com.github.pagehelper.Page;
import com.honam.simple.vo.User;

import java.util.List;

/**
 * Created by Honam on 2016/7/15.
 */
public interface Hello {
    public String sayHello();

    public List<User> getUser();

    public Page<User> getUserByPage();
}
