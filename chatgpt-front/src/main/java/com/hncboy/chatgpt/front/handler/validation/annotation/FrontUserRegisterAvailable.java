package com.hncboy.chatgpt.front.handler.validation.annotation;

import com.hncboy.chatgpt.front.handler.validation.impl.FrontUserRegisterAvailableValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前端用户注册校验器
 *
 * @author CoDeleven
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FrontUserRegisterAvailableValidator.class)
public @interface FrontUserRegisterAvailable {

    String message() default "校验不通过";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
