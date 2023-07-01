package com.hncboy.beehive.cell.midjourney.service;

import cn.hutool.core.lang.Pair;
import com.hncboy.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hncboy
 * @date 2023/5/19
 * Discord 业务接口
 */
public interface DiscordService {

    /**
     * imagine
     *
     * @param prompt               prompt
     * @param midjourneyProperties Midjourney 配置
     * @return 调用结果
     */
    Pair<Boolean, String> imagine(String prompt, MidjourneyProperties midjourneyProperties);

    /**
     * upscale
     *
     * @param discordMessageId     Discord 消息 id
     * @param index                位置
     * @param discordMessageHash   Discord 消息 hash
     * @param midjourneyProperties Midjourney 配置
     * @return 调用结果
     */
    Pair<Boolean, String> upscale(String discordMessageId, int index, String discordMessageHash, MidjourneyProperties midjourneyProperties);

    /**
     * variation
     * 需要关闭 settings 里的 remix mode，不然收不到回调消息，remix mode 的接口参数不一样
     *
     * @param discordMessageId     Discord 消息 id
     * @param index                位置
     * @param discordMessageHash   Discord 消息 hash
     * @param midjourneyProperties Midjourney 配置
     * @return 调用结果
     */
    Pair<Boolean, String> variation(String discordMessageId, int index, String discordMessageHash, MidjourneyProperties midjourneyProperties);

    /**
     * 上传图片
     *
     * @param fileName             文件名
     * @param multipartFile        文件
     * @param midjourneyProperties Midjourney 配置
     * @return 调用结果
     */
    Pair<Boolean, String> uploadImage(String fileName, MultipartFile multipartFile, MidjourneyProperties midjourneyProperties);

    /**
     * 描述图片
     *
     * @param uploadFileName       上传的文件名
     * @param midjourneyProperties Midjourney 配置
     * @return 调用结果
     */
    Pair<Boolean, String> describe(String uploadFileName, MidjourneyProperties midjourneyProperties);
}
