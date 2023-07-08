package com.hncboy.beehive.cell.midjourney.handler.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.util.FileUtil;
import com.hncboy.beehive.base.util.PictureUtil;
import com.hncboy.beehive.cell.midjourney.constant.MidjourneyConstant;
import com.hncboy.beehive.cell.midjourney.handler.MidjourneyTaskQueueHandler;
import com.hncboy.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import com.hncboy.beehive.cell.midjourney.util.MjRoomMessageUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/18
 * 抽象消息处理器
 */
@Slf4j
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
     * 下载原图
     *
     * @param discordImageUrl discord 图片地址
     * @param roomMjMsgId     房间消息 id
     * @return 图片名称
     */
    public String downloadOriginalImage(String discordImageUrl, Long roomMjMsgId) {
        String fileExtension = FileUtil.getFileExtension(discordImageUrl);
        // 文件前缀
        String filePrefix = MidjourneyConstant.ORIGINAL_FILE_PREFIX.concat(String.valueOf(roomMjMsgId)).concat(StrPool.DOT);

        String fileName = filePrefix.concat(fileExtension);
        FileUtil.downloadFromUrl(discordImageUrl, filePrefix.concat(fileExtension));
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
            // 获取 Discord 图片
            roomMidjourneyMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
            // 下载原图
            roomMidjourneyMsgDO.setOriginalImageName(downloadOriginalImage(roomMidjourneyMsgDO.getDiscordImageUrl(), roomMidjourneyMsgDO.getId()));
            // 下载缩略图
            roomMidjourneyMsgDO.setCompressedImageName(MjRoomMessageUtil.downloadCompressedImage(roomMidjourneyMsgDO.getOriginalImageName(), roomMidjourneyMsgDO.getId()));
        }

        // 结束执行中任务
        midjourneyTaskQueueHandler.finishExecuteTask(roomMidjourneyMsgDO.getId());
    }
}
