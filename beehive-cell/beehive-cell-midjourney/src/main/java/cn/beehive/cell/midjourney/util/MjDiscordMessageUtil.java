package cn.beehive.cell.midjourney.util;

import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.cell.midjourney.domain.bo.MjDiscordMessageBO;
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
    private static final String IMAGINE_CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@(\\d+)> \\((.*?)\\)";

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
    public static MjDiscordMessageBO matchImagineMessage(Message message) {
        Pattern pattern = Pattern.compile(IMAGINE_CONTENT_REGEX);
        Matcher matcher = pattern.matcher(message.getContentRaw());
        if (!matcher.find()) {
            return null;
        }
        MjDiscordMessageBO messageBO = new MjDiscordMessageBO();
        messageBO.setAction(MjMsgActionEnum.IMAGINE);
        messageBO.setPrompt(matcher.group(1));
        messageBO.setStatus(matcher.group(3));
        return messageBO;
    }
}
