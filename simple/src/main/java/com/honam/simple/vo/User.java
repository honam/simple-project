package com.honam.simple.vo;

import com.honam.base.api.annotation.APIField;
import com.honam.base.api.annotation.APIValueObject;

import java.io.Serializable;

/**
 * Created by xuhaonan on 15/11/27.
 */
@APIValueObject("用户对象")
public class User implements Serializable {
    @APIField("用户ID")
    private Long id;
    @APIField("用户名")
    private String userName;
    @APIField("用户密码")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
