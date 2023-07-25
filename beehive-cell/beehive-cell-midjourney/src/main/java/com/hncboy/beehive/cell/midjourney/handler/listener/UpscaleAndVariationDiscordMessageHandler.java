package com.hncboy.beehive.cell.midjourney.handler.listener;

import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.cell.midjourney.domain.bo.MidjourneyDiscordMessageBO;
import com.hncboy.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import com.hncboy.beehive.cell.midjourney.util.MjRoomMessageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * UV 命令消息处理器
 * <p>
 * upscale
 * Midjounery Discord 消息变更 - Midjourney Bot: **[1660606423536029699] star,sky --v 5 --s 750** - <@1013002753796219000> (fast)
 * Midjounery Discord 消息接收 - Midjourney Bot: **[1660633960119070722] star,sky --v 5 --s 750** - Image #3 <@1013002753796219000>
 * <p>
 * Variation
 * Midjounery Discord 消息变更 - Midjourney Bot: **[1660639025269575682] star, boy --v 5 --s 750** - Remix by <@1013002753796219000> (fast)
 * Midjounery Discord 消息接收 - Midjourney Bot: **[1660639025269575682] star, boy --v 5 --s 750** - Variations by <@1013002753796219000> (fast)
 */
@Component
public class UpscaleAndVariationDiscordMessageHandler extends AbstractDiscordMessageHandler {

    @Override
    public void onMessageReceived(Message message) {
        MidjourneyDiscordMessageBO messageBO = MjDiscordMessageUtil.matchUpscaleAndVariationMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id，这是父消息
        Long parentRoomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        if (Objects.isNull(parentRoomMjMsgId)) {
            return;
        }
        RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO = roomMidjourneyMsgService.getById(parentRoomMjMsgId);
        if (Objects.isNull(parentRoomMidjourneyMsgDO)) {
            return;
        }

        // 如果是 upscale 消息
        if (messageBO.getAction() == MidjourneyMsgActionEnum.UPSCALE) {
            // 找对应的 upscale 消息
            RoomMidjourneyMsgDO upscaleRoomMidjourneyMsgDO = roomMidjourneyMsgService.getOne(new LambdaQueryWrapper<RoomMidjourneyMsgDO>()
                    // 只会有等待接收状态，在 onMessageUpdate 因为区分不出是 u 几，所以不做处理
                    .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED)
                    .eq(RoomMidjourneyMsgDO::getAction, MidjourneyMsgActionEnum.UPSCALE)
                    .eq(RoomMidjourneyMsgDO::getType, MessageTypeEnum.ANSWER)
                    .eq(RoomMidjourneyMsgDO::getUvIndex, messageBO.getIndex())
                    // 找子消息
                    .eq(RoomMidjourneyMsgDO::getUvParentId, parentRoomMjMsgId));
            if (Objects.isNull(upscaleRoomMidjourneyMsgDO)) {
                return;
            }

            // 更新 upscale 消息
            finishImageTask(upscaleRoomMidjourneyMsgDO, message);
            roomMidjourneyMsgService.updateById(upscaleRoomMidjourneyMsgDO);

            // 更新父消息
            parentRoomMidjourneyMsgDO.setUUseBit(MjRoomMessageUtil.setUpscaleUse(parentRoomMidjourneyMsgDO.getUUseBit(), upscaleRoomMidjourneyMsgDO.getUvIndex(), MidjourneyMsgActionEnum.UPSCALE));
            roomMidjourneyMsgService.updateById(parentRoomMidjourneyMsgDO);
        }

        // 如果是 variation 消息
        if (messageBO.getAction() == MidjourneyMsgActionEnum.VARIATION) {
            // 找对应的 variation 消息，这里不知道 v 几，理论上只会有一条
            RoomMidjourneyMsgDO variationRoomMidjourneyMsgDO = roomMidjourneyMsgService.getOne(new LambdaQueryWrapper<RoomMidjourneyMsgDO>()
                    // 只会有等待接收状态
                    .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED)
                    .eq(RoomMidjourneyMsgDO::getAction, MidjourneyMsgActionEnum.VARIATION)
                    .eq(RoomMidjourneyMsgDO::getType, MessageTypeEnum.ANSWER)
                    // 找子消息
                    .eq(RoomMidjourneyMsgDO::getUvParentId, parentRoomMjMsgId));
            if (Objects.isNull(variationRoomMidjourneyMsgDO)) {
                return;
            }

            // 更新 variation 消息
            finishImageTask(variationRoomMidjourneyMsgDO, message);
            roomMidjourneyMsgService.updateById(variationRoomMidjourneyMsgDO);
            // 不更新父消息
        }
    }

    @Override
    public void onMessageUpdate(Message message) {
        // 这里能接收到 upscale 消息，但是不处理，因为这里区分不出是 U 几，虽然可以更新 MJ_WAIT_RECEIVED 状态的数据，因为理论上一个用户只允许一个 MJ_WAIT_RECEIVED 状态的数据，但是不考虑了。
        // 这里能接收到 variation 消息，但是不处理，因为这里区分不出是 V 几，虽然可以更新 MJ_WAIT_RECEIVED 状态的数据，因为理论上一个用户只允许一个 MJ_WAIT_RECEIVED 状态的数据，但是不考虑了。
        // variation 消息在 discord 会有过程，但是这里监听不到，所以处理不了
    }
}
