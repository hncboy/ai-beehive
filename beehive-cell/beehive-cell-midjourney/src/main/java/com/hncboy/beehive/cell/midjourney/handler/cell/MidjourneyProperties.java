package com.hncboy.beehive.cell.midjourney.handler.cell;

import com.hncboy.beehive.base.domain.entity.CellConfigDO;
import com.hncboy.beehive.base.domain.entity.CellDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.CellStatusEnum;
import com.hncboy.beehive.cell.core.hander.CellConfigHandler;
import com.hncboy.beehive.cell.core.hander.CellHandler;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjounery 配置
 */
@Data
public class MidjourneyProperties {

    private MidjourneyProperties() {
    }

    /**
     * 初始化
     *
     * @return Midjounery 配置
     */
    public static MidjourneyProperties init() {
        MidjourneyProperties midjourneyProperties = new MidjourneyProperties();

        // 发布 Midjourney 图纸重启才生效
        CellDO cellDO = CellHandler.getCell(CellCodeEnum.MIDJOURNEY);
        if (Objects.isNull(cellDO) || cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            return midjourneyProperties;
        }
        Map<String, CellConfigDO> cellConfigMap = CellConfigHandler.getCellConfigMap(CellCodeEnum.MIDJOURNEY);
        midjourneyProperties.setGuildId((cellConfigMap.get(MidjourneyCellConfigCodeEnum.GUILD_ID.getCode()).getDefaultValue()));
        midjourneyProperties.setChannelId(cellConfigMap.get(MidjourneyCellConfigCodeEnum.CHANNEL_ID.getCode()).getDefaultValue());
        midjourneyProperties.setUserToken(cellConfigMap.get(MidjourneyCellConfigCodeEnum.USER_TOKEN.getCode()).getDefaultValue());
        midjourneyProperties.setBotToken(cellConfigMap.get(MidjourneyCellConfigCodeEnum.BOT_TOKEN.getCode()).getDefaultValue());
        midjourneyProperties.setMjBotName(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MJ_BOT_NAME.getCode()).getDefaultValue());
        midjourneyProperties.setUserAgent(cellConfigMap.get(MidjourneyCellConfigCodeEnum.USER_AGENT.getCode()).getDefaultValue());
        midjourneyProperties.setMaxWaitQueueSize(Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_WAIT_QUEUE_SIZE.getCode()).getDefaultValue()));
        midjourneyProperties.setMaxExecuteQueueSize(Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_EXECUTE_QUEUE_SIZE.getCode()).getDefaultValue()));
        midjourneyProperties.setMaxFileSize(Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_FILE_SIZE.getCode()).getDefaultValue()));
        return midjourneyProperties;
    }

    /**
     * 服务器 id
     */
    private String guildId;

    /**
     * 频道 id
     */
    private String channelId;

    /**
     * 用户 token
     */
    private String userToken;

    /**
     * 机器人 token
     */
    private String botToken;

    /**
     * Midjourney 机器人的名称，需要一致
     */
    private String mjBotName = "Midjourney Bot";

    /**
     * 调用 discord 接口时的 user-agent
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";

    /**
     * 等待队列最大长度
     */
    private Integer maxWaitQueueSize = 5;

    /**
     * 执行队列最大长度
     */
    private Integer maxExecuteQueueSize = 2;

    /**
     * 最大文件大小，用于 describe 上传图片，单位字节
     * 默认 6M
     */
    private Integer maxFileSize = 1024 * 1024 * 6;

    /**
     * Discord Api Url
     */
    private String discordApiUrl = "https://discord.com/api/v9/interactions";

    /**
     * Discord 上传图片 Url
     */
    private String discordUploadUrl;

    /**
     * 获取 Discord 上传图片 Url
     */
    public String getDiscordUploadUrl() {
        return "https://discord.com/api/v9/channels/" + channelId + "/attachments";
    }
}
