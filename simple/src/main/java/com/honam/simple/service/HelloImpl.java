package com.honam.simple.service;

import com.honam.simple.imterface.Hello;
import org.springframework.stereotype.Service;

/**
 * Created by Honam on 2016/7/15.
 */
@Service
public class HelloImpl implements Hello {
    @Override
    public String sayHello() {
        return "Hello.";
    }
}
