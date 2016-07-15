package com.honam.simple;

import com.honam.BaseTest;
import com.honam.simple.imterface.Hello;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
