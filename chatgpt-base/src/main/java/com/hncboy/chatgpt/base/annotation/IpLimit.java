package com.hncboy.chatgpt.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lizhongyuan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IpLimit {
    /**
     * 最大访问次数
     *
     * @return ip 最大访问次数
     */
    int count() default Integer.MAX_VALUE;

    /**
     * 过期时间 默认 5s 也就是 5s 能访问 count 次
     *
     * @return ip 过期时间
     */
    int expire() default 5;

}
