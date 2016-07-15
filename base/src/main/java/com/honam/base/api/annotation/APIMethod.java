package com.honam.base.api.annotation;


import com.honam.base.api.vo.ApiAuthor;
import com.honam.base.api.vo.ApiCategory;

import java.lang.annotation.*;

/**
 * Created by xuhaonan on 15/11/27.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface APIMethod {

    //接口说明
    String desc() default "";

    //接口分类
    ApiCategory category() default ApiCategory.OTHERS;

    String version() default "1.0";

    //开发者
    ApiAuthor[] apiAuthors();

}
