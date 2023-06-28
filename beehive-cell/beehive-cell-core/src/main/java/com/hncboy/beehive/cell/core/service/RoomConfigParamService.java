package com.hncboy.beehive.cell.core.service;

import com.hncboy.beehive.base.domain.entity.RoomConfigParamDO;
import com.hncboy.beehive.cell.core.domain.request.RoomConfigParamEditRequest;
import com.hncboy.beehive.cell.core.domain.vo.RoomConfigParamVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间配置参数业务接口
 */
public interface RoomConfigParamService extends IService<RoomConfigParamDO> {

    /**
     * 根据房间 id 查询房间配置参数
     *
     * @param roomId 房间 id
     * @return 房间配置项参数列表
     */
    List<RoomConfigParamVO> list(Long roomId);

    /**
     * 编辑房间配置参数
     *
     * @param request 房间配置参数编辑请求
     * @return 房间配置项参数列表
     */
    List<RoomConfigParamVO> edit(RoomConfigParamEditRequest request);
}
