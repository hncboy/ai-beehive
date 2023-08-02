package com.hncboy.beehive.cell.core.cache;

import com.hncboy.beehive.base.domain.entity.RoomConfigParamDO;
import com.hncboy.beehive.cell.core.service.RoomConfigParamService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/8
 * 填写注释
 */
public class RoomConfigParamCache {

    /**
     * 获取房间配置项参数
     * TODO 缓存
     *
     * @param roomId 房间 id
     * @return 房间配置项
     */
    public static List<RoomConfigParamDO> getRoomConfigParam(Long roomId) {
        RoomConfigParamService roomConfigParamService = SpringUtil.getBean(RoomConfigParamService.class);
        return roomConfigParamService.list(new LambdaQueryWrapper<RoomConfigParamDO>()
                .eq(RoomConfigParamDO::getRoomId, roomId));
    }
}
