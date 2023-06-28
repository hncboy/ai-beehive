package com.hncboy.beehive.cell.bing.service;

import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import com.hncboy.beehive.cell.bing.domain.bo.BingRoomBO;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间业务接口
 */
public interface RoomBingService extends IService<RoomBingDO> {

    /**
     * 获取房间信息
     *
     * @param roomId      房间 id
     * @param sendRequest 发送消息请求
     * @return 房间业务信息
     */
    BingRoomBO getRoom(Long roomId, RoomBingMsgSendRequest sendRequest);

    /**
     * 刷新房间业务信息
     *
     * @param bingRoomBO 房间业务信息
     * @return 房间业务信息
     */
    BingRoomBO refreshRoom(BingRoomBO bingRoomBO);
}
