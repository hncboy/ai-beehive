package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.base.util.FileUtil;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import net.dv8tion.jda.api.entities.Message;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/18
 * 消息处理器
 */
public abstract class DiscordMessageHandler {

    @Resource
    protected RoomMjMsgService roomMjMsgService;

    @Resource
    protected MjTaskQueueHandler mjTaskQueueHandler;

    @Resource
    protected MidjourneyConfig midjourneyConfig;

    /**
     * 接收到新消息
     *
     * @param message 消息
     */
    abstract void onMessageReceived(Message message);

    /**
     * 已发送的消息更新
     *
     * @param message 消息
     */
    abstract void onMessageUpdate(Message message);

    /**
     * 下载图片
     *
     * @param discordImageUrl discord 图片地址
     * @param roomMjMsgId     房间消息 id
     * @return 图片名称
     */
    public String downloadImage(String discordImageUrl, Long roomMjMsgId) {
        String fileName = roomMjMsgId + ".png";
        FileUtil.downloadFromUrl(discordImageUrl, midjourneyConfig.getImageLocation() + fileName);
        return fileName;
    }

    /**
     * 完成图片任务
     *
     * @param roomMjMsgDO 房间消息
     * @param message     discord 消息
     */
    public void finishImageTask(RoomMjMsgDO roomMjMsgDO, Message message) {
        roomMjMsgDO.setDiscordMessageId(message.getId());
        roomMjMsgDO.setDiscordFinishTime(new Date());
        if (CollectionUtil.isEmpty(message.getAttachments())) {
            // MJ 返回空图片
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_FAILURE);
        } else {
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_SUCCESS);
            // 获取图片
            roomMjMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
            // 下载图片
            roomMjMsgDO.setImageName(downloadImage(roomMjMsgDO.getDiscordImageUrl(), roomMjMsgDO.getId()));
        }

        // 结束执行中任务
        mjTaskQueueHandler.finishExecuteTask(roomMjMsgDO.getId());
    }
}
