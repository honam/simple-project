package com.honam.api.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaonan on 15/11/27.
 */
public class ApiMethod implements Serializable{

    private String name;
    private String category;
    private boolean supportHttp;
    private String description;
    private List<String> apiUsers;
    private List<String> apiAuthors;
    private String[] url;
    List<ApiParam> param = new ArrayList<ApiParam>();
    private ApiReturn apiReturn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSupportHttp() {
        return supportHttp;
    }

    public void setSupportHttp(boolean supportHttp) {
        this.supportHttp = supportHttp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getApiUsers() {
        return apiUsers;
    }

    public void setApiUsers(List<String> apiUsers) {
        this.apiUsers = apiUsers;
    }

    public List<String> getApiAuthors() {
        return apiAuthors;
    }

    public void setApiAuthors(List<String> apiAuthors) {
        this.apiAuthors = apiAuthors;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
        this.url = url;
    }

    public List<ApiParam> getParam() {
        return param;
    }

    public void setParam(List<ApiParam> param) {
        this.param = param;
    }

    public ApiReturn getApiReturn() {
        return apiReturn;
    }

    public void setApiReturn(ApiReturn apiReturn) {
        this.apiReturn = apiReturn;
    }
}
