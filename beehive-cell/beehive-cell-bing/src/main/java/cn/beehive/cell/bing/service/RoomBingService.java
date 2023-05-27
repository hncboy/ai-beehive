package cn.beehive.cell.bing.service;

import cn.beehive.base.domain.entity.RoomBingDO;
import cn.beehive.cell.bing.domain.bo.BingApiSendThrottlingResultBO;
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
     * @param roomId 房间 id
     * @return 房间信息
     */
    RoomBingDO getRoom(Long roomId);

    /**
     * 更新房间累计提问次数
     *
     * @param roomBingDO 房间信息
     * @param throttling bing 限流信息
     */
    void updateRoomMessageNum(RoomBingDO roomBingDO, BingApiSendThrottlingResultBO throttling);
}
