package com.honam.simple;

import com.honam.BaseTest;
import com.honam.simple.imterface.Hello;
import com.honam.simple.vo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Honam on 2016/7/15.
 */
public class HelloTest extends BaseTest{

    @Autowired
    private Hello hello;

    @Test
    public void test(){
        System.out.println(hello.sayHello());
    }

    @Test
    public void getUser(){
        List<User> user = hello.getUser();
        System.out.println(user.size());
    }
}
