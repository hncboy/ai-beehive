package cn.beehive.cell.midjourney.handler.listener;

import cn.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import cn.beehive.base.enums.MidjourneyMsgStatusEnum;
import cn.beehive.cell.midjourney.domain.bo.MjDiscordMessageBO;
import cn.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
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
public class ImagineDiscordMessageHandler extends DiscordMessageHandler {

    @Override
    public void onMessageReceived(Message message) {
        MjDiscordMessageBO messageBO = MjDiscordMessageUtil.matchImagineMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id
        Long roomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        if (Objects.isNull(roomMjMsgId)) {
            return;
        }
        RoomMidjourneyMsgDO roomMidjourneyMsgDO = roomMidjourneyMsgService.getById(roomMjMsgId);
        if (Objects.isNull(roomMidjourneyMsgDO)) {
            return;
        }


        // 开始处理，不考虑消息乱序和丢失的情况
        if ("Waiting to start".equals(messageBO.getStatus())) {
            // 队列排队再回调好像没有这个，还需确认一下
            roomMidjourneyMsgDO.setDiscordStartTime(new Date());
            roomMidjourneyMsgDO.setStatus(MidjourneyMsgStatusEnum.MJ_IN_PROGRESS);
            roomMidjourneyMsgDO.setDiscordMessageId(message.getId());
        }
        // 处理成功
        else {
            finishImageTask(roomMidjourneyMsgDO, message);
        }

        roomMidjourneyMsgDO.setResponseContent(message.getContentRaw());
        roomMidjourneyMsgService.updateById(roomMidjourneyMsgDO);
    }

    @Override
    public void onMessageUpdate(Message message) {
        MjDiscordMessageBO messageBO = MjDiscordMessageUtil.matchImagineMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id
        Long roomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        if (Objects.isNull(roomMjMsgId)) {
            return;
        }
        RoomMidjourneyMsgDO roomMidjourneyMsgDO = roomMidjourneyMsgService.getById(roomMjMsgId);
        if (Objects.isNull(roomMidjourneyMsgDO)) {
            return;
        }

        roomMidjourneyMsgDO.setDiscordMessageId(message.getId());
        roomMidjourneyMsgDO.setResponseContent(message.getContentRaw());
        roomMidjourneyMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
        // 下载图片
        roomMidjourneyMsgDO.setImageName(downloadImage(roomMidjourneyMsgDO.getDiscordImageUrl(), roomMidjourneyMsgDO.getId()));
        roomMidjourneyMsgService.updateById(roomMidjourneyMsgDO);
    }
}
