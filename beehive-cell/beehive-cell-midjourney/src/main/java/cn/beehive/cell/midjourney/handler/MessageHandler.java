package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.util.FileUtil;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import jakarta.annotation.Resource;
import net.dv8tion.jda.api.entities.Message;

/**
 * @author hncboy
 * @date 2023/5/18
 * 消息处理器
 */
public abstract class MessageHandler {

    @Resource
    protected RoomMjMsgService roomMjMsgService;

    @Resource
    protected MjTaskQueueHandler mjTaskQueueHandler;

    @Resource
    protected MidjourneyConfig midjourneyConfig;

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
     * 下载图片
     *
     * @param discordImageUrl discord 图片地址
     * @param roomMjMsgId     房间消息 id
     * @return 图片名称
     */
    public String downloadImage(String discordImageUrl, Long roomMjMsgId) {
        String fileName = roomMjMsgId + ".png";
        FileUtil.downloadLocalFromUrl(discordImageUrl, midjourneyConfig.getImageLocation() + fileName);
        return fileName;
    }
}
