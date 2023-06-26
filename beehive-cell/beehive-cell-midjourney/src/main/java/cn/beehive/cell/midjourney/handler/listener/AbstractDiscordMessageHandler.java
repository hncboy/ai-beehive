package cn.beehive.cell.midjourney.handler.listener;

import cn.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import cn.beehive.base.enums.MidjourneyMsgStatusEnum;
import cn.beehive.base.util.FileUtil;
import cn.beehive.cell.midjourney.handler.MidjourneyTaskQueueHandler;
import cn.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import net.dv8tion.jda.api.entities.Message;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/18
 * 抽象消息处理器
 */
public abstract class AbstractDiscordMessageHandler {

    @Resource
    protected RoomMidjourneyMsgService roomMidjourneyMsgService;

    @Resource
    protected MidjourneyTaskQueueHandler midjourneyTaskQueueHandler;

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
        String fileName = "mj".concat(String.valueOf(roomMjMsgId)).concat(".png");
        FileUtil.downloadFromUrl(discordImageUrl, fileName);
        return fileName;
    }

    /**
     * 完成图片任务
     *
     * @param roomMidjourneyMsgDO 房间消息
     * @param message             discord 消息
     */
    public void finishImageTask(RoomMidjourneyMsgDO roomMidjourneyMsgDO, Message message) {
        roomMidjourneyMsgDO.setDiscordMessageId(message.getId());
        roomMidjourneyMsgDO.setDiscordFinishTime(new Date());
        if (CollectionUtil.isEmpty(message.getAttachments())) {
            // MJ 返回空图片
            roomMidjourneyMsgDO.setStatus(MidjourneyMsgStatusEnum.MJ_FAILURE);
        } else {
            roomMidjourneyMsgDO.setStatus(MidjourneyMsgStatusEnum.MJ_SUCCESS);
            // 获取图片
            roomMidjourneyMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
            // 下载图片
            roomMidjourneyMsgDO.setImageName(downloadImage(roomMidjourneyMsgDO.getDiscordImageUrl(), roomMidjourneyMsgDO.getId()));
        }

        // 结束执行中任务
        midjourneyTaskQueueHandler.finishExecuteTask(roomMidjourneyMsgDO.getId());
    }
}
