package com.hncboy.beehive.cell.openai.module.chat.parser;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/7/10
 * AccessToken 对话错误码枚举
 */
@AllArgsConstructor
public enum AccessTokenChatErrorCodeEnum {

    /**
     * 我们的系统检测到您的系统有异常活动，请稍后再试。
     * {
     *   "detail": "Our systems have detected unusual activity from your system. Please try again later."
     * }
     */
    SYSTEM_DETECTED_UNUSUAL_ACTIVITY("", "我们的系统检测到您的系统有异常活动，请等待一段时间后再试，请勿多次尝试",
            "Our systems have detected unusual activity from your system. Please try again later."),

    /**
     * 同一时间只能有一个消息
     * {
     *   "detail": "Only one message at a time. Please allow any other responses to complete before sending another message, or wait one minute."
     * }
     */
    ONLY_ONE_MESSAGE("", "同一时间只能回复一个消息，当前已经有消息在回复中，请稍后再试",
            "Only one message at a time. Please allow any other responses to complete before sending another message, or wait one minute."),

    /**
     * 解析 JWT 失败
     * {
     *   "detail": {
     *     "code": "invalid_jwt",
     *     "message": "Could not parse your authentication token. Please try signing in again.",
     *     "param": null,
     *     "type": "invalid_request_error"
     *   }
     * }
     */
    INVALID_JWT("invalid_jwt", "无效的 JWT，请勿多次尝试", null),

    /**
     * AccessToken 过期
     * {
     *   "detail": {
     *     "code": "token_expired",
     *     "message": "Your authentication token has expired. Please try signing in again.",
     *     "param": null,
     *     "type": "invalid_request_error"
     *   }
     * }
     */
    TOKEN_EXPIRED("token_expired", "AccessToken 过期，请勿多次尝试", null),

    /**
     * 对话不存在或被删除
     * {
     * 	"detail": {
     * 		"message": "Conversation not found",
     * 		"code": "conversation_not_found"
     *   }
     * }
     */
    CONVERSATION_NOT_FOUND("conversation_not_found", "该对话不存在或已删除，请新建房间，请勿多次尝试", null);

    // TODO GPT4 次数达到上限

    @Getter
    private final String code;

    @Getter
    private final String message;

    /**
     * detail 字段信息
     */
    @Getter
    private final String detail;

    /**
     * code 作为 key，封装为 Map
     */
    public static final Map<String, AccessTokenChatErrorCodeEnum> CODE_MAP = Stream
            .of(AccessTokenChatErrorCodeEnum.values())
            .filter(e -> StrUtil.isNotBlank(e.getCode()))
            .collect(Collectors.toMap(AccessTokenChatErrorCodeEnum::getCode, Function.identity()));

    /**
     * detail 作为 key，封装为 Map
     */
    public static final Map<String, AccessTokenChatErrorCodeEnum> DETAIL_MAP = Stream
            .of(AccessTokenChatErrorCodeEnum.values())
            .filter(e -> StrUtil.isNotBlank(e.getDetail()))
            .collect(Collectors.toMap(AccessTokenChatErrorCodeEnum::getDetail, Function.identity()));
}
