package com.hncboy.chatgpt.base.constant;

/**
 * @author hncboy
 * @date 2023/3/27 21:41
 * 应用相关常量
 */
public interface ApplicationConstant {

    /**
     * ADMIN 路径前缀
     */
    String ADMIN_PATH_PREFIX = "admin";
    /**
     * 用户登录-JWT携带参数名称：注册类型code
     */
    String FRONT_JWT_REGISTER_TYPE_CODE = "registerTypeCode";
    /**
     * 用户登录-JWT携带参数名称：登录账号（邮箱/手机号）
     */
    String FRONT_JWT_USERNAME = "username";
    /**
     * 用户登录-JWT携带参数名称：基础用户ID
     */
    String FRONT_JWT_BASE_USER_ID = "baseUserId";
}
