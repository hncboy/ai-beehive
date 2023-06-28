package com.hncboy.beehive.web.cache;

import com.hncboy.beehive.web.domain.bo.FrontUserBO;

/**
 * @author hncboy
 * @date 2023/6/27
 * 前端用户缓存
 */
public class FrontUserCache {

    /**
     * 获取用户业务对象
     *
     * @return 用户业务对象
     */
    public FrontUserBO getUser() {
        return new FrontUserBO();
    }
}
