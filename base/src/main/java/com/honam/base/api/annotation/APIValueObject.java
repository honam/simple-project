package com.honam.base.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by xuhaonan on 15/11/27.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface APIValueObject {
    String value() default "";
}
