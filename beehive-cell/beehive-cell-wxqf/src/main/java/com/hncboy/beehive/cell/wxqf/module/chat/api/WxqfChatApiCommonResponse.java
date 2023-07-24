package com.hncboy.beehive.cell.wxqf.module.chat.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 API 通用响应参数
 */
@Data
public class WxqfChatApiCommonResponse {

    /**
     * 本轮对话的 id
     */
    private String id;

    /**
     * 回包类型
     * chat.completion：多轮对话返回
     */
    private String object;

    /**
     * 时间戳
     */
    private Integer created;

    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("sentence_id")
    private Integer sentenceId;

    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("is_end")
    private Boolean isEnd;

    /**
     * 当前生成的结果是否被截断
     */
    @JsonProperty("is_truncated")
    private Boolean isTruncated;

    /**
     * 对话返回结果
     */
    private String result;

    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    @JsonProperty("need_clear_history")
    private Boolean needClearHistory;

    /**
     * token 统计信息，token 数 = 汉字数+单词数*1.3 （仅为估算逻辑）
     */
    private Usage usage;

    /**
     * token 统计信息
     */
    @Data
    public static class Usage {

        /**
         * 问题 tokens 数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 回答 tokens 数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * tokens 总数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
