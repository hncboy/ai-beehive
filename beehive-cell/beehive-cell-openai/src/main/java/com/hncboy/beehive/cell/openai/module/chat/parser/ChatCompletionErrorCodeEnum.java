package com.hncboy.beehive.cell.openai.module.chat.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/6/15
 * 对话错误码枚举
 * @see <a href="https://platform.openai.com/docs/guides/error-codes/api-errors"/>API 错误</a>
 */
@AllArgsConstructor
public enum ChatCompletionErrorCodeEnum {

    /**
     * tokens 超过限制
     * {
     *   "error": {
     *     "message": "This model's maximum context length is 4097 tokens. However, you requested 4871 tokens (3871 in the messages, 1000 in the completion). Please reduce the length of the messages or completion.",
     *     "type": "invalid_request_error",
     *     "param": "messages",
     *     "code": "context_length_exceeded"
     *   }
     * }
     */
    CONTEXT_LENGTH_EXCEEDED("context_length_exceeded", "上下文长度超过限制"),

    /**
     * 无效 ApiKey
     * {
     *     "error": {
     *         "message": "",
     *         "type": "invalid_request_error",
     *         "param": null,
     *         "code": "invalid_api_key"
     *     }
     * }
     */
    INVALID_API_KEY("invalid_api_key", "无效的 API Key"),

    /**
     * 余额不足
     * {
     *     "error": {
     *         "message": "You exceeded your current quota, please check your plan and billing details.",
     *         "type": "insufficient_quota",
     *         "param": null,
     *         "code": null
     *     }
     * }
     */
    INSUFFICIENT_QUOTA("insufficient_quota", "余额不足"),

    /**
     * 模型负载过高
     * {
     *   "error": {
     *     "message": "That model is currently overloaded with other requests. You can retry your request, or contact us through our help center at help.openai.com if the error persists. (Please include the request ID 3a1a3ed9167dfdd2a4661abbc46a6cc8 in your message.)",
     *     "type": "server_error",
     *     "param": null,
     *     "code": null
     *   }
     * }
     */
    server_error("server_error", "该模型已经超过负载，请稍后再试"),

    /**
     * 账单未处于活动状态
     * {
     *   "error": {
     *         "message": "Your account is not active, please check your billing details on our website.",
     *         "type": "billing_not_active",
     *         "param": null,
     *         "code": null
     *     }
     * }
     */
    BILLING_NOT_ACTIVE("billing_not_active", "账单未激活");

    @Getter
    private final String code;

    @Getter
    private final String message;

    /**
     * code 作为 key，封装为 Map
     */
    public static final Map<String, ChatCompletionErrorCodeEnum> CODE_MAP = Stream
            .of(ChatCompletionErrorCodeEnum.values())
            .collect(Collectors.toMap(ChatCompletionErrorCodeEnum::getCode, Function.identity()));

}
