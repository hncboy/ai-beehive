package com.hncboy.beehive.cell.midjourney.util;

import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.hncboy.beehive.cell.midjourney.domain.bo.MidjourneyDiscordMessageBO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import net.dv8tion.jda.api.entities.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hncboy
 * @date 2023/5/20
 * Midjourney Discord 消息工具类
 */
public class MjDiscordMessageUtil {

    /**
     * imagine 消息正则
     */
    private static final Pattern IMAGINE_CONTENT_REGEX_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\* - <@(\\d+)> \\((.*?)\\)");

    /**
     * uv 消息正则
     */
    private static final Pattern MJ_UV_CONTENT_REGEX_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\* - (.*?) by <@(\\d+)> \\((.*?)\\)");

    /**
     * u 消息正则
     */
    private static final Pattern MJ_U_CONTENT_REGEX_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\* - Image #(\\d) <@(\\d+)>");

    /**
     * describe 消息替换其中的链接正则
     */
    private static final String MJ_DESCRIBE_HREF_REPLACE_REGEX = "\\[(.*?)]\\(https://goo.gl/search.*?\\)";

    /**
     * 提取 imagine 消息中的消息 id
     *
     * @param finalPrompt 最终 Prompt
     * @return 消息 id
     */
    public static Long findMsgIdByFinalPrompt(String finalPrompt) {
        // 只会提取第一组 []
        String msgIdStr = CharSequenceUtil.subBetween(finalPrompt, "[", "]");
        if (StrUtil.isBlank(msgIdStr)) {
            return null;
        }
        return Long.valueOf(msgIdStr);
    }

    /**
     * 匹配 imagine 消息
     *
     * @param message 原始消息
     * @return imagine 消息
     */
    public static MidjourneyDiscordMessageBO matchImagineMessage(Message message) {
        Matcher matcher = IMAGINE_CONTENT_REGEX_PATTERN.matcher(message.getContentRaw());
        if (!matcher.find()) {
            return null;
        }
        MidjourneyDiscordMessageBO messageBO = new MidjourneyDiscordMessageBO();
        messageBO.setAction(MidjourneyMsgActionEnum.IMAGINE);
        messageBO.setPrompt(matcher.group(1));
        messageBO.setStatus(matcher.group(3));
        return messageBO;
    }

    /**
     * 匹配 uv 消息
     *
     * @param message 原始消息
     * @return uv 消息
     */
    public static MidjourneyDiscordMessageBO matchUpscaleAndVariationMessage(Message message) {
        Matcher matcher = MJ_UV_CONTENT_REGEX_PATTERN.matcher(message.getContentRaw());
        if (!matcher.find()) {
            return matchUpscaleContent(message);
        }
        MidjourneyDiscordMessageBO messageBO = new MidjourneyDiscordMessageBO();
        messageBO.setPrompt(matcher.group(1));
        String matchAction = matcher.group(2);
        // 判断是不是 Variation
        messageBO.setAction(matchAction.startsWith("Variation") ? MidjourneyMsgActionEnum.VARIATION : MidjourneyMsgActionEnum.UPSCALE);
        messageBO.setStatus(matcher.group(4));
        return messageBO;
    }

    /**
     * 匹配 upscale 消息
     *
     * @param message 原始消息
     * @return upscale 消息
     */
    private static MidjourneyDiscordMessageBO matchUpscaleContent(Message message) {
        Matcher matcher = MJ_U_CONTENT_REGEX_PATTERN.matcher(message.getContentRaw());
        if (!matcher.find()) {
            return null;
        }
        MidjourneyDiscordMessageBO messageBO = new MidjourneyDiscordMessageBO();
        messageBO.setAction(MidjourneyMsgActionEnum.UPSCALE);
        messageBO.setPrompt(matcher.group(1));
        messageBO.setStatus("complete");
        messageBO.setIndex(Integer.parseInt(matcher.group(2)));
        return messageBO;
    }

    /**
     * 替换 describe 消息中的链接
     *
     * @param prompt 原始消息
     * @return 替换后的消息
     */
    public static String replacePrompt(String prompt) {
        return prompt.replaceAll(MJ_DESCRIBE_HREF_REPLACE_REGEX, "$1");
    }
}
