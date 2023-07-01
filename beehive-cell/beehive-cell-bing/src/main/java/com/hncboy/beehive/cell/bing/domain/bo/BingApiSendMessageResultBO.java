package com.hncboy.beehive.cell.bing.domain.bo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * BingApi 发送消息响应参数
 */
@Data
public class BingApiSendMessageResultBO {

    /**
     * 消息内容
     */
    private String text;

    /**
     * user 或 bot
     */
    private String author;

    /**
     * 消息创建时间
     */
    private Date createdAt;

    /**
     * en-us
     */
    private String regin;

    /**
     * 建议
     */
    private List<SuggestedResponse> suggestedResponses;

    /**
     * bot 回复的详细信息
     */
    private List<SourceAttribution> sourceAttributions;

    @Data
    public static class SuggestedResponse {

        /**
         * 消息内容
         */
        private String text;
    }

    @Data
    public static class SourceAttribution {

        /**
         * 更多的链接
         */
        private String seeMoreUrl;

        /**
         * 展示的名称
         */
        private String providerDisplayName;
    }
}
