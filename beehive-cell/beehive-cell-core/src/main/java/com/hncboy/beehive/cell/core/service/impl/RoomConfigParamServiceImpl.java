package com.hncboy.beehive.cell.core.service.impl;

import com.hncboy.beehive.base.domain.entity.RoomConfigParamDO;
import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.base.mapper.RoomConfigParamMapper;
import com.hncboy.beehive.cell.core.cache.RoomConfigParamCache;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.domain.request.RoomConfigParamEditRequest;
import com.hncboy.beehive.cell.core.domain.vo.CellConfigVO;
import com.hncboy.beehive.cell.core.domain.vo.RoomConfigParamVO;
import com.hncboy.beehive.cell.core.hander.RoomConfigParamHandler;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.converter.RoomConfigParamConverter;
import com.hncboy.beehive.cell.core.service.CellConfigService;
import com.hncboy.beehive.cell.core.service.RoomConfigParamService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间配置参数业务接口实现类
 */
@Service
public class RoomConfigParamServiceImpl extends ServiceImpl<RoomConfigParamMapper, RoomConfigParamDO> implements RoomConfigParamService {

    @Resource
    private CellConfigService cellConfigService;

    @Override
    public List<RoomConfigParamVO> list(Long roomId) {
        // 校验房间信息
        RoomDO roomDO = RoomHandler.checkRoomExist(roomId);

        // 根据图纸编码获取所有配置项
        List<CellConfigVO> cellConfigVOList = cellConfigService.listCellConfig(roomDO.getCellCode().getCode());

        // 获取房间配置参数值
        Map<String, RoomConfigParamDO> roomConfigParamMap = getRoomConfigParamValueMap(roomId);

        // 遍历所有配置项进行重新封装
        List<RoomConfigParamVO> roomConfigParamVOList = new ArrayList<>();
        for (CellConfigVO cellConfigVO : cellConfigVOList) {
            RoomConfigParamVO roomConfigParamVO = RoomConfigParamConverter.INSTANCE.cellConfigVoToVo(cellConfigVO);

            // 如果是用户自己填的
            if (roomConfigParamMap.containsKey(roomConfigParamVO.getCellConfigCode())) {
                roomConfigParamVO.setIsUseDefaultValue(false);
                roomConfigParamVO.setValue(roomConfigParamMap.get(roomConfigParamVO.getCellConfigCode()).getValue());
            }
            // 用系统的默认值
            else {
                roomConfigParamVO.setIsUseDefaultValue(true);
                roomConfigParamVO.setValue(roomConfigParamVO.getDefaultValue());
            }

            roomConfigParamVOList.add(roomConfigParamVO);
        }
        return roomConfigParamVOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<RoomConfigParamVO> edit(RoomConfigParamEditRequest request) {
        // 校验房间信息
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(request.getRoomId());

        // 检查房间配置项参数请求
        List<RoomConfigParamBO> waitSaveRoomConfigParamBOList = RoomConfigParamHandler.checkRoomConfigParamRequest(roomDO.getCellCode(), request.getRoomConfigParams(), true)
                // 过滤出用户自己填的
                .stream().filter(bo -> !bo.getIsUseDefaultValue()).toList();

        // 获取房间配置参数 Map
        Map<String, RoomConfigParamDO> existRoomConfigParamMap = getRoomConfigParamValueMap(request.getRoomId());

        // 待保存或更新的房间配置参数列表
        List<RoomConfigParamDO> waitSaveOrUpdateRoomConfigParamDOList = new ArrayList<>();

        for (RoomConfigParamBO roomConfigParamBO : waitSaveRoomConfigParamBOList) {
            // 获取已存在的房间配置参数
            RoomConfigParamDO existRoomConfigParamDO = existRoomConfigParamMap.get(roomConfigParamBO.getCellConfigCode());

            // 不存在的原来的房间配置项就新增
            if (Objects.isNull(existRoomConfigParamDO)) {
                waitSaveOrUpdateRoomConfigParamDOList.add(RoomConfigParamConverter.INSTANCE.boToEntity(roomConfigParamBO, request.getRoomId()));
            } else {
                // 更新已有的配置
                existRoomConfigParamDO.setValue(roomConfigParamBO.getValue());
                waitSaveOrUpdateRoomConfigParamDOList.add(existRoomConfigParamDO);
                // 在 Map 中移除指定配置项 code
                existRoomConfigParamMap.remove(roomConfigParamBO.getCellConfigCode());
            }
        }

        // 批量新增或更新配置
        saveOrUpdateBatch(waitSaveOrUpdateRoomConfigParamDOList);

        // 还剩下已存在的说明是要被删除的，直接全部删除
        if (CollectionUtil.isNotEmpty(existRoomConfigParamMap)) {
            removeBatchByIds(existRoomConfigParamMap.values().stream().map(RoomConfigParamDO::getId).collect(Collectors.toList()));
        }

        return list(request.getRoomId());
    }

    /**
     * 获取房间配置参数 Map
     *
     * @param roomId 房间 id
     * @return 房间配置参数 Map
     */
    private Map<String, RoomConfigParamDO> getRoomConfigParamValueMap(Long roomId) {
        List<RoomConfigParamDO> roomConfigParamDOList = RoomConfigParamCache.getRoomConfigParam(roomId);
        // cellConfigCode 为 key，RoomConfigParamDO 为 value
        return roomConfigParamDOList.stream().collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, Function.identity()));
    }
}