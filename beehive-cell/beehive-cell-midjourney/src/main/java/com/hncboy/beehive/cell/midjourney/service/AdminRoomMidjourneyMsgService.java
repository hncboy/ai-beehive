package com.hncboy.beehive.cell.midjourney.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;

/**
 * @author hncboy
 * @date 2023/7/1
 * 管理端-Midjourney 房间消息业务接口
 */
public interface AdminRoomMidjourneyMsgService extends IService<RoomMidjourneyMsgDO> {

    /**
     * 标记错误消息
     *
     * @param msgId 消息 id
     */
    void markErrorMessage(Long msgId);
}
