package com.shaw.dolores.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//是否 进行oauth验证登录
public @interface OAuthPassport {
    boolean validate() default true;
}
