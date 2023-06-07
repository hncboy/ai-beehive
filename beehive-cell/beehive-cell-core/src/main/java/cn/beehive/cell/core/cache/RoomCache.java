package cn.beehive.cell.core.cache;

import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.cell.core.service.RoomService;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author hncboy
 * @date 2023/6/8
 * 房间相关缓存
 */
public class RoomCache {

    /**
     * 获取房间信息
     * TODO 缓存
     *
     * @param roomId 房间 id
     * @return 房间信息
     */
    public static RoomDO getRoom(Long roomId) {
        RoomService roomService = SpringUtil.getBean(RoomService.class);
        return roomService.getById(roomId);
    }
}
