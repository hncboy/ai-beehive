package cn.beehive.cell.midjourney.handler.cell;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.cell.core.hander.CellConfigHandler;
import cn.beehive.cell.core.hander.CellHandler;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjounery 配置
 */
@Data
@Component("midjourneyProperties")
public class MidjourneyProperties implements InitializingBean {

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
     * 图片存储路径
     */
    private String imageLocation;

    /**
     * 等待队列最大长度
     */
    private Integer maxWaitQueueSize = 5;

    /**
     * 执行队列最大长度
     */
    private Integer maxExecuteQueueSize = 2;

    /**
     * 最大文件大小，用于 descibe 上传图片，单位字节
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

    @Override
    public void afterPropertiesSet() {
        CellDO cellDO = CellHandler.getCell(CellCodeEnum.MIDJOURNEY);
        if (Objects.isNull(cellDO) || cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            return;
        }

        // 因为 Discord 配置需要初始化，而且切换配置可能会导致一些操作失败，所以先从数据库取出配置初始化，不实时查询，不过有些参数有需要的也可以改成实时查询配置
        // TODO 判断数据是否存在
        Map<String, CellConfigDO> cellConfigMap = CellConfigHandler.getCellConfigMap(CellCodeEnum.MIDJOURNEY);
        guildId = cellConfigMap.get(MidjourneyCellConfigCodeEnum.GUILD_ID.getCode()).getDefaultValue();
        channelId = cellConfigMap.get(MidjourneyCellConfigCodeEnum.CHANNEL_ID.getCode()).getDefaultValue();
        userToken = cellConfigMap.get(MidjourneyCellConfigCodeEnum.USER_TOKEN.getCode()).getDefaultValue();
        botToken = cellConfigMap.get(MidjourneyCellConfigCodeEnum.BOT_TOKEN.getCode()).getDefaultValue();
        mjBotName = cellConfigMap.get(MidjourneyCellConfigCodeEnum.MJ_BOT_NAME.getCode()).getDefaultValue();
        userAgent = cellConfigMap.get(MidjourneyCellConfigCodeEnum.USER_AGENT.getCode()).getDefaultValue();
        imageLocation = cellConfigMap.get(MidjourneyCellConfigCodeEnum.IMAGE_LOCATION.getCode()).getDefaultValue();
        maxWaitQueueSize = Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_EXECUTE_QUEUE_SIZE.getCode()).getDefaultValue());
        maxExecuteQueueSize = Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_EXECUTE_QUEUE_SIZE.getCode()).getDefaultValue());
        maxFileSize = Integer.valueOf(cellConfigMap.get(MidjourneyCellConfigCodeEnum.MAX_FILE_SIZE.getCode()).getDefaultValue());
    }
}
