package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.cell.midjourney.domain.bo.MjDiscordMessageBO;
import cn.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        RoomMjMsgDO roomMjMsgDO = roomMjMsgService.getById(roomMjMsgId);
        if (Objects.isNull(roomMjMsgDO)) {
            return;
        }


        // 开始处理，不考虑消息乱序和丢失的情况
        if ("Waiting to start".equals(messageBO.getStatus())) {
            roomMjMsgDO.setDiscordStartTime(new Date());
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_IN_PROGRESS);
            roomMjMsgDO.setDiscordMessageId(message.getId());
        }
        // 处理成功
        else {
            finishImageTask(roomMjMsgDO, message);
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
        if (Objects.isNull(roomMjMsgId)) {
            return;
        }
        RoomMjMsgDO roomMjMsgDO = roomMjMsgService.getById(roomMjMsgId);
        if (Objects.isNull(roomMjMsgDO)) {
            return;
        }

        roomMjMsgDO.setDiscordMessageId(message.getId());
        roomMjMsgDO.setResponseContent(message.getContentRaw());
        roomMjMsgDO.setDiscordImageUrl(message.getAttachments().get(0).getUrl());
        // 下载图片
        roomMjMsgDO.setImageName(downloadImage(roomMjMsgDO.getDiscordImageUrl(), roomMjMsgDO.getId()));
        roomMjMsgService.updateById(roomMjMsgDO);
    }
}
