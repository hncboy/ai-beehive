package com.hncboy.beehive.cell.midjourney.service;

import cn.hutool.core.lang.Pair;

/**
 * @author hncboy
 * @date 2023/6/5
 * Discord 发送服务接口
 */
@FunctionalInterface
public interface DiscordSendService {

    /**
     * 发送请求
     * @return 调用结果
     */
    Pair<Boolean, String> sendRequest();
}
