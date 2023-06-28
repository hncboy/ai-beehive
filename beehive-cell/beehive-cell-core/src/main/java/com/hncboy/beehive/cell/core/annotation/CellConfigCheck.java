package com.hncboy.beehive.cell.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hncboy
 * @date 2023/6/10
 * Cell 配置项校验
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CellConfigCheck {

    /**
     * 房间 id
     */
    String roomId() default "";
}
