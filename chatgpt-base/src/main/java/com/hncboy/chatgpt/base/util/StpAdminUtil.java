package com.hncboy.chatgpt.base.util;

import cn.dev33.satoken.stp.StpLogic;

/**
 * @author hncboy
 * @date 2023-4-16
 * 填写注释
 */
public class StpAdminUtil {

    /**
     * 账号类型标识
     */
    public static final String TYPE = "admin";

    /**
     * 底层的 StpLogic 对象
     */
    public static StpLogic stpLogic = new StpLogic(TYPE);

    /**
     * 检验当前会话是否已经登录，如未登录，则抛出异常
     */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    // ------------------- 登录相关操作 -------------------

    // --- 登录

    /**
     * 会话登录
     * @param id 账号id，建议的类型：（long | int | String）
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }
}
