package com.honam.api.controller;

import com.honam.api.service.ApiScanEngine;
import com.honam.api.vo.ApiMethod;
import com.honam.api.vo.ApiValueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by xuhaonan on 15/11/27.
 */
@Controller
@RequestMapping("api")
public class ApiController {
    @Autowired
    private ApiScanEngine apiScanEngine;

    private static Map<String, List<ApiMethod>> categoryApiMap = null;

    @RequestMapping("openAPI")
    @ResponseBody
    public Map<String, List<ApiMethod>> openAPI(){
        if(categoryApiMap == null){
            categoryApiMap = apiScanEngine.getAllAPIMethod();
        }
        return categoryApiMap;
    }

    @RequestMapping("getValueObjectDesc")
    @ResponseBody
    public ApiValueObject getValueObjectDesc(String className) throws ClassNotFoundException{
        return apiScanEngine.getValueObjectDesc(className);
    }
}
