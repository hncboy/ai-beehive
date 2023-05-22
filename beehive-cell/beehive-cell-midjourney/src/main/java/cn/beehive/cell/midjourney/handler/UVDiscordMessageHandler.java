package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.cell.midjourney.domain.bo.MjDiscordMessageBO;
import cn.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import cn.beehive.cell.midjourney.util.MjRoomMessageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * UV 命令消息处理器
 * Midjounery Discord 消息变更 - Midjourney Bot: **[1660606423536029699] star,sky --v 5 --s 750** - <@1013002753796219000> (fast)
 * Midjounery Discord 消息接收 - Midjourney Bot: **[1660633960119070722] star,sky --v 5 --s 750** - Image #3 <@1013002753796219000>
 */
@Component
public class UVDiscordMessageHandler extends DiscordMessageHandler {

    @Override
    public void onMessageReceived(Message message) {
        MjDiscordMessageBO messageBO = MjDiscordMessageUtil.matchUVMessage(message);
        if (Objects.isNull(messageBO)) {
            return;
        }

        // 提取房间消息 id，这是父消息
        Long parentRoomMjMsgId = MjDiscordMessageUtil.findMsgIdByFinalPrompt(messageBO.getPrompt());
        if (Objects.isNull(parentRoomMjMsgId)) {
            return;
        }
        RoomMjMsgDO parentRoomMjMsgDO = roomMjMsgService.getById(parentRoomMjMsgId);
        if (Objects.isNull(parentRoomMjMsgDO)) {
            return;
        }

        // 如果是 upscale 消息
        if (messageBO.getAction() == MjMsgActionEnum.UPSCALE) {
            // 找对应的 upscale 消息
            RoomMjMsgDO upscaleRoomMjMsgDO = roomMjMsgService.getOne(new LambdaQueryWrapper<RoomMjMsgDO>()
                    // 只会有等待接收状态，在 onMessageUpdate 因为区分不出是 u 几，所以不做处理
                    .eq(RoomMjMsgDO::getStatus, MjMsgStatusEnum.MJ_WAIT_RECEIVED)
                    .eq(RoomMjMsgDO::getAction, MjMsgActionEnum.UPSCALE)
                    .eq(RoomMjMsgDO::getType, MessageTypeEnum.ANSWER)
                    .eq(RoomMjMsgDO::getUvIndex, messageBO.getIndex())
                    // 找子消息
                    .eq(RoomMjMsgDO::getUvParentId, parentRoomMjMsgId));
            if (Objects.isNull(upscaleRoomMjMsgDO)) {
                return;
            }

            // 更新 upscale 消息
            upscaleRoomMjMsgDO.setDiscordMessageId(message.getId());
            finishImageTask(upscaleRoomMjMsgDO, message);
            roomMjMsgService.updateById(upscaleRoomMjMsgDO);

            // 更新父消息
            parentRoomMjMsgDO.setUvUseBit(MjRoomMessageUtil.setUVUse(parentRoomMjMsgDO.getUvUseBit(), upscaleRoomMjMsgDO.getUvIndex(), MjMsgActionEnum.UPSCALE));
            roomMjMsgService.updateById(parentRoomMjMsgDO);
        }
    }

    @Override
    public void onMessageUpdate(Message message) {
        // 这里能接收到 U 消息，但是不处理，因为这里区分不出是 U 几，虽然可以更新 MJ_WAIT_RECEIVED 状态的数据，因为理论上一个用户只允许一个 MJ_WAIT_RECEIVED 状态的数据，但是不考虑了。
    }
}
