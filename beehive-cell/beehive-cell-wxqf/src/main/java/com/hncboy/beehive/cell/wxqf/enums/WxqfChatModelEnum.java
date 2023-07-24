package com.hncboy.beehive.cell.wxqf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话模型枚举
 */
@AllArgsConstructor
public enum WxqfChatModelEnum {

    /**
     * ERNIE-Bot 是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力
     */
    ERNIE_BOT("ERNIE-Bot"),

    /**
     * ERNIE-Bot-turbo 是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力，响应速度更快
     */
    ERNIE_BOT_TURBO("ERNIE-Bot-turbo"),

    /**
     * BLOOMZ-7B 是业内知名的大语言模型，由 Hugging Face 研发并开源，能够以 46 种语言和 13 种编程语言输出文本
     */
    BLOOMZ_7B("BLOOMZ-7B");

    @Getter
    private final String code;
}
