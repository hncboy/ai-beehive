package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.cell.midjourney.domain.bo.MjDiscordMessageBO;
import cn.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Imagine 命令消息处理器
 * 消息接收 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (Waiting to start)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (0%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (15%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (31%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (46%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (62%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (78%) (fast)
 * 消息变更 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (93%) (fast)
 * 消息接收 - Midjourney Bot: **[6601101751104431] blue sky --v 5 --s 250** - <@1013002753796219000> (fast)
 */

@Component
public class ImagineMessageHandler extends MessageHandler {

    @Override
    public void onMessageReceived(Message message) {
        MjDiscordMessageBO messageBO = MjDiscordMessageUtil.matchImagineMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id
        Long roomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        RoomMjMsgDO roomMjMsgDO = roomMjMsgService.getById(roomMjMsgId);
        if (Objects.isNull(roomMjMsgDO)) {
            return;
        }

        roomMjMsgDO.setDiscordMessageId(message.getId());

        // 根据状态操作
        if ("Waiting to start".equals(messageBO.getStatus())) {
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_IN_PROGRESS);
        } else {
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_SUCCESS);
            // 获取图片
            roomMjMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
            // 下载图片
            roomMjMsgDO.setImageName(downloadImage(roomMjMsgDO.getDiscordImageUrl(), roomMjMsgDO.getId()));
            // 结束执行中任务
            mjTaskQueueHandler.finishExecuteTask(roomMjMsgId);
        }

        roomMjMsgDO.setResponseContent(message.getContentRaw());
        roomMjMsgService.updateById(roomMjMsgDO);
    }

    @Override
    public void onMessageUpdate(Message message) {
        MjDiscordMessageBO messageBO = MjDiscordMessageUtil.matchImagineMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id
        Long roomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        RoomMjMsgDO roomMjMsgDO = roomMjMsgService.getById(roomMjMsgId);
        roomMjMsgDO.setResponseContent(message.getContentRaw());
        roomMjMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
        // 下载图片
        roomMjMsgDO.setImageName(downloadImage(roomMjMsgDO.getDiscordImageUrl(), roomMjMsgDO.getId()));
        roomMjMsgService.updateById(roomMjMsgDO);
    }
}
