package cn.beehive.cell.midjourney.listener;

import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.handler.DescribeDiscordMessageHandler;
import cn.beehive.cell.midjourney.handler.ImagineDiscordMessageHandler;
import cn.beehive.cell.midjourney.handler.UVDiscordMessageHandler;
import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Discord 消息监听
 */
@Slf4j
@Component
public class DiscordMessageListener extends ListenerAdapter {

    @Resource
    private MidjourneyConfig midjourneyConfig;

    @Resource
    private ImagineDiscordMessageHandler imagineDiscordMessageHandler;

    @Resource
    private UVDiscordMessageHandler uvMessageHandler;

    @Resource
    private DescribeDiscordMessageHandler describeMessageHandler;

    /**
     * 是否忽略消息
     *
     * @param message   消息
     * @param eventName 时间名
     * @return 是否忽略
     */
    private boolean isIgnoreMessage(Message message, String eventName) {
        // 频道不一样忽略
        if (ObjectUtil.notEqual(midjourneyConfig.getChannelId(), message.getChannel().getId())) {
            return true;
        }

        String authorName = message.getAuthor().getName();
        log.info("Midjounery Discord {} - {}: {}", eventName, authorName, message.getContentRaw());

        // 消息指定机器人的消息忽略
        return ObjectUtil.notEqual(midjourneyConfig.getMjBotName(), authorName);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        Message message = event.getMessage();
        if (isIgnoreMessage(message, "消息变更")) {
            return;
        }

        if (Objects.nonNull(message.getInteraction()) && "describe".equals(message.getInteraction().getName())) {
            describeMessageHandler.onMessageUpdate(message);
        } else if (Objects.nonNull(message.getInteraction()) && "imagine".equals(message.getInteraction().getName())) {
            imagineDiscordMessageHandler.onMessageUpdate(message);
        } else {
            uvMessageHandler.onMessageUpdate(message);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (isIgnoreMessage(message, "消息接收")) {
            return;
        }

        if (MessageType.SLASH_COMMAND.equals(message.getType()) || MessageType.DEFAULT.equals(message.getType())) {
            imagineDiscordMessageHandler.onMessageReceived(message);
        } else if (MessageType.INLINE_REPLY.equals(message.getType()) && message.getReferencedMessage() != null) {
            uvMessageHandler.onMessageReceived(message);
        }
    }
}