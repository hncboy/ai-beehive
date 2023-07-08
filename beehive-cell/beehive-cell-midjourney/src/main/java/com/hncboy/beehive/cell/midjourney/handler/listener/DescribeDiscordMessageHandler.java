package com.hncboy.beehive.cell.midjourney.handler.listener;

import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.cell.midjourney.constant.MidjourneyConstant;
import com.hncboy.beehive.cell.midjourney.util.MjDiscordMessageUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Describe 命令消息处理器
 * 先输出消息接收再输出消息变更，消息接收无法识别
 * Midjounery Discord 消息接收 - Midjourney Bot:
 * Midjounery Discord 消息变更 - Midjourney Bot:
 */
@Component
public class DescribeDiscordMessageHandler extends AbstractDiscordMessageHandler {

    @Override
    public void onMessageReceived(Message message) {
        // 消息接收无法识别
    }

    @Override
    public void onMessageUpdate(Message message) {
        List<MessageEmbed> embeds = message.getEmbeds();
        if (embeds.isEmpty()) {
            return;
        }
        MessageEmbed messageEmbed = embeds.get(0);
        if (Objects.isNull(messageEmbed.getImage())) {
            return;
        }
        String discordImageUrl = messageEmbed.getImage().getUrl();
        if (StrUtil.isBlank(discordImageUrl)) {
            return;
        }

        // 截取文件名+后缀
        int hashStartIndex = discordImageUrl.lastIndexOf("/");
        // 截取文件名，文件名就是房间消息 id
        String mjMsgId = CharSequenceUtil.subBefore(discordImageUrl.substring(hashStartIndex + 1), ".", true);
        if (StrUtil.isBlank(mjMsgId) || !StrUtil.startWith(mjMsgId, MidjourneyConstant.DESCRIBE_ORIGINAL_FILE_PREFIX)) {
            return;
        }

        // 找对应的房间消息记录
        RoomMidjourneyMsgDO roomMidjourneyMsgDO = roomMidjourneyMsgService.getById(mjMsgId.replace(MidjourneyConstant.DESCRIBE_ORIGINAL_FILE_PREFIX, ""));
        if (Objects.isNull(roomMidjourneyMsgDO)) {
            return;
        }

        // 返回的提示语
        String prompt = messageEmbed.getDescription();

        roomMidjourneyMsgDO.setDiscordMessageId(message.getId());
        roomMidjourneyMsgDO.setDiscordFinishTime(new Date());
        if (StrUtil.isBlank(prompt)) {
            // MJ 返回提示语为空，不知道什么情况会出现
            roomMidjourneyMsgDO.setStatus(MidjourneyMsgStatusEnum.MJ_FAILURE);
        } else {
            roomMidjourneyMsgDO.setResponseContent(MjDiscordMessageUtil.replacePrompt(prompt));
            roomMidjourneyMsgDO.setStatus(MidjourneyMsgStatusEnum.MJ_SUCCESS);
            roomMidjourneyMsgDO.setDiscordImageUrl(discordImageUrl);
        }

        // 结束执行中任务
        midjourneyTaskQueueHandler.finishExecuteTask(roomMidjourneyMsgDO.getId());
        roomMidjourneyMsgService.update(roomMidjourneyMsgDO, new LambdaUpdateWrapper<RoomMidjourneyMsgDO>()
                .eq(RoomMidjourneyMsgDO::getId, roomMidjourneyMsgDO.getId())
                .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED));
    }
}
