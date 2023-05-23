package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DescribeDiscordMessageHandler extends DiscordMessageHandler {

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
        if (StrUtil.isBlank(mjMsgId) || !StrUtil.startWith(mjMsgId, "describe_")) {
            return;
        }

        // 找对应的房间消息记录
        RoomMjMsgDO roomMjMsgDO = roomMjMsgService.getById(mjMsgId.replace("describe_", ""));
        if (Objects.isNull(roomMjMsgDO)) {
            return;
        }

        // 返回的提示语
        String prompt = messageEmbed.getDescription();

        roomMjMsgDO.setPrompt(prompt);
        roomMjMsgDO.setDiscordMessageId(message.getId());
        roomMjMsgDO.setDiscordFinishTime(new Date());
        if (StrUtil.isBlank(prompt)) {
            // MJ 返回提示语为空，不知道什么情况会出现
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_FAILURE);
        } else {
            roomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_SUCCESS);
            roomMjMsgDO.setDiscordImageUrl(discordImageUrl);
        }

        // 结束执行中任务
        mjTaskQueueHandler.finishExecuteTask(roomMjMsgDO.getId());
        roomMjMsgService.updateById(roomMjMsgDO);
    }
}
