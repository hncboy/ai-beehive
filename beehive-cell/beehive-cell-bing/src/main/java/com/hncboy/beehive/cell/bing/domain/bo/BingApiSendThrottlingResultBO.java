package com.hncboy.beehive.cell.bing.domain.bo;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/26
 * BingApi 节流响应参数
 */
@Data
public class BingApiSendThrottlingResultBO {

    /**
     * 用户最大提问次数
     * TODO 有时候是 5，有时候是 10，20，但是一般都是 5，官方的 20 要怎么复现？
     */
    private Integer maxNumUserMessagesInConversation;

    /**
     * 用户当前提问次数
     */
    private Integer numUserMessagesInConversation;
}
