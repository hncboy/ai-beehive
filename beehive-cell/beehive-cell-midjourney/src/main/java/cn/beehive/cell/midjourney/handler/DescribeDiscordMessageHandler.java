package cn.beehive.cell.midjourney.handler;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/5/18
 * Describe 命令消息处理器
 */
@Component
@RequiredArgsConstructor
public class DescribeDiscordMessageHandler extends DiscordMessageHandler {

    @Override
    public void onMessageReceived(Message message) {
    }

    @Override
    public void onMessageUpdate(Message message) {

    }
}
