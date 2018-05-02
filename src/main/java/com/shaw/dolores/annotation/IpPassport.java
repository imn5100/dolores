package com.shaw.dolores.annotation;

import java.lang.annotation.*;

/**
 * Created by shaw on 2016/12/21 0021.
 */
//被javadoc记录
@Documented
//可继承
@Inherited
//方法注解
@Target(ElementType.METHOD)
//运行时注解
@Retention(RetentionPolicy.RUNTIME)
public @interface IpPassport {
    boolean validate() default true;
}
