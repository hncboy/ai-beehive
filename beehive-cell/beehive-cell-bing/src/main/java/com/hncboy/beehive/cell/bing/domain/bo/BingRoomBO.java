package com.hncboy.beehive.cell.bing.domain.bo;

import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/28
 * Bing 房间业务信息
 */
@Data
public class BingRoomBO {

    /**
     * 房间实体信息
     */
    private RoomBingDO roomBingDO;

    /**
     * 是否是新话题
     */
    private Boolean isNewTopic;

    /**
     * 刷新房间原因
     */
    private String refreshRoomReason;
}
