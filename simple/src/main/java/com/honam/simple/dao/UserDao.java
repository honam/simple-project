package com.honam.simple.dao;

import com.honam.simple.models.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Honam on 2016/7/15.
 */
@Repository
public class UserDao extends SimpleBaseDao{

    public List<User> getUser(){
        StringBuilder sb = new StringBuilder();
        sb.append("select * from user");
        return getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<User>(User.class));
    }

}
