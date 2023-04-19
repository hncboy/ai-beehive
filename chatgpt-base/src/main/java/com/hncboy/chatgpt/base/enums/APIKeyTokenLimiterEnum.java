package com.hncboy.chatgpt.base.enums;

import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * API-KEY模式下字数限制枚举
 * 该枚举不适合序列化
 *
 * @author CoDeleven
 */
@AllArgsConstructor
@Getter
public enum APIKeyTokenLimiterEnum {
    /**
     * GPT3.5 Token限制文档
     * @see <a href="https://platform.openai.com/docs/models/gpt-3-5">GPT3.5 Token限制文档</a>
     */
    GPT_3_5_TURBO(ChatCompletion.Model.GPT_3_5_TURBO, 4096),
    GPT_3_5_TURBO_0301(ChatCompletion.Model.GPT_3_5_TURBO, 4096),
    /**
     * GPT4 Token限制文档
     * @see <a href="https://platform.openai.com/docs/models/gpt-4">GPT4Token限制文档</a>
     */
    GPT_4(ChatCompletion.Model.GPT_4, 8192),
    GPT_4_0314(ChatCompletion.Model.GPT_4_0314, 8192),
    GPT_4_32K(ChatCompletion.Model.GPT_4_32K, 32768),
    GPT_4_32K_0314(ChatCompletion.Model.GPT_4_32K_0314, 32768);

    // 代码里可能会使用到Jar包里的Model
    private final ChatCompletion.Model model;
    // Model对应的字数限制情况
    private final int tokenLimit;

    /**
     * 根据Jar包的Model获取Token数量限制
     *
     * @param outerJarModel Jar包的Model （{@link com.unfbx.chatgpt.entity.chat.ChatCompletion.Model}）
     * @return 限制的Token数量
     */
    public static int getTokenLimitByOuterJarModel(ChatCompletion.Model outerJarModel) {
        for (APIKeyTokenLimiterEnum modelItem : APIKeyTokenLimiterEnum.values()) {
            if(modelItem.model == outerJarModel) {
                return modelItem.tokenLimit;
            }
        }
        return 0;
    }

    /**
     * 根据Jar包的Model的name，获取Token数量限制
     *
     * @param outerJarModelName Jar包的Model的name （{@link com.unfbx.chatgpt.entity.chat.ChatCompletion.Model}）
     * @return 限制的Token数量
     */
    public static int getTokenLimitByOuterJarModelName(String outerJarModelName) {
        for (APIKeyTokenLimiterEnum modelItem : APIKeyTokenLimiterEnum.values()) {
            if(Objects.equals(modelItem.model.getName(), outerJarModelName)) {
                return modelItem.tokenLimit;
            }
        }
        return 0;
    }

    /**
     * 根据 外部Jar包的Model名称 查询本系统内的字数限制枚举，从而判断 contextTokenCount 是否超出限制
     *
     * @param outerJarModelName Jar包的Model的name（{@link com.unfbx.chatgpt.entity.chat.ChatCompletion.Model}）
     * @param contextTokenCount 此消息的上下文Token字数总和
     * @return 是否超出Token数量限制
     */
    public static boolean exceedsLimit(String outerJarModelName, int contextTokenCount) {
        int tokenLimits = getTokenLimitByOuterJarModelName(outerJarModelName);
        if(contextTokenCount >= tokenLimits) {
            return true;
        }
        return false;
    }
}
