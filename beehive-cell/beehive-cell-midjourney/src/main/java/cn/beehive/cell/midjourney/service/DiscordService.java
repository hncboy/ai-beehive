package cn.beehive.cell.midjourney.service;

import cn.hutool.core.lang.Pair;

/**
 * @author hncboy
 * @date 2023/5/19
 * Discord 业务接口
 */
public interface DiscordService {

    /**
     * imagine
     *
     * @param prompt prompt
     * @return 调用结果
     */
    Pair<Boolean, String> imagine(String prompt);

    /**
     * upscale
     *
     * @param discordMessageId   Discord 消息 id
     * @param index              位置
     * @param discordMessageHash Discord 消息 hash
     * @return 调用结果
     */
    Pair<Boolean, String> upscale(String discordMessageId, int index, String discordMessageHash);

    /**
     * variation
     * 需要关闭 settings 里的 remix mode，不然收不到回调消息，remix mode 的接口参数不一样
     *
     * @param discordMessageId   Discord 消息 id
     * @param index              位置
     * @param discordMessageHash Discord 消息 hash
     * @return 调用结果
     */
    Pair<Boolean, String> variation(String discordMessageId, int index, String discordMessageHash);
}
