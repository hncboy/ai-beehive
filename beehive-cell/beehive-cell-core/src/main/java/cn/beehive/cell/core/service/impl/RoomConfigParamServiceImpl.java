package cn.beehive.cell.core.service.impl;

import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.base.mapper.RoomConfigParamMapper;
import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.domain.bo.RoomConfigParamBO;
import cn.beehive.cell.core.domain.request.RoomConfigParamEditRequest;
import cn.beehive.cell.core.domain.vo.RoomConfigParamVO;
import cn.beehive.cell.core.hander.CellConfigPermissionHandler;
import cn.beehive.cell.core.hander.RoomConfigParamHandler;
import cn.beehive.cell.core.hander.RoomHandler;
import cn.beehive.cell.core.hander.converter.RoomConfigParamConverter;
import cn.beehive.cell.core.service.RoomConfigParamService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间配置参数业务接口实现类
 */
@Service
public class RoomConfigParamServiceImpl extends ServiceImpl<RoomConfigParamMapper, RoomConfigParamDO> implements RoomConfigParamService {

    @Override
    public List<RoomConfigParamVO> list(Long roomId) {
        RoomDO roomDO = RoomHandler.checkRoomExist(roomId);
        // 根据配置项查询配置项权限参数列表
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigPermissionHandler.listCellConfigPermission(roomDO.getCellCode());

        // 根据房间 id 查询房间配置参数
        List<RoomConfigParamDO> roomConfigParamDOList = list(new LambdaQueryWrapper<RoomConfigParamDO>()
                .select(RoomConfigParamDO::getCellConfigCode, RoomConfigParamDO::getValue)
                .eq(RoomConfigParamDO::getRoomId, roomId));
        // 将房间配置项参数列表转换为 Map
        Map<String, String> roomConfigParamMap = roomConfigParamDOList.stream().collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, RoomConfigParamDO::getValue));

        // 遍历所有权限参数进行封装
        List<RoomConfigParamVO> roomConfigParamVOList = new ArrayList<>();
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            RoomConfigParamVO roomConfigParamVO = RoomConfigParamConverter.INSTANCE.cellConfigPermissionBOToVO(cellConfigPermissionBO);
            roomConfigParamVO.setIsUseDefaultValue(true);
            roomConfigParamVO.setValue(roomConfigParamVO.getDefaultValue());

            // 如果是用户自己填的
            if (roomConfigParamMap.containsKey(roomConfigParamVO.getCellConfigCode())) {
                roomConfigParamVO.setValue(roomConfigParamMap.get(roomConfigParamVO.getCellConfigCode()));
                roomConfigParamVO.setIsUseDefaultValue(true);
            }

            roomConfigParamVOList.add(roomConfigParamVO);
        }
        return roomConfigParamVOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<RoomConfigParamVO> edit(RoomConfigParamEditRequest request) {
        RoomDO roomDO = RoomHandler.checkRoomExist(request.getRoomId());
        // 检查房间配置项参数请求
        List<RoomConfigParamBO> waitSaveRoomConfigParamBOList = RoomConfigParamHandler.checkRoomConfigParamRequest(roomDO.getCellCode(), request.getRoomConfigParams(), true)
                // 过滤出用户自己填的
                .stream().filter(bo -> !bo.getIsUseDefaultValue()).toList();

        // 查询已存在的所有房间配置
        List<RoomConfigParamDO> existRoomConfigParamDOList = list(new LambdaQueryWrapper<RoomConfigParamDO>()
                .eq(RoomConfigParamDO::getRoomId, request.getRoomId()));
        // 转为 Map，key 为 cellConfigCode
        Map<String, RoomConfigParamDO> existRoomConfigParamMap = existRoomConfigParamDOList.stream()
                .collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, roomConfigParamDO -> roomConfigParamDO));

        // 待保存或更新的房间配置参数列表
        List<RoomConfigParamDO> waitSaveOrUpdateRoomConfigParamDOList = new ArrayList<>();

        // 保存房间配置参数
        for (RoomConfigParamBO roomConfigParamBO : waitSaveRoomConfigParamBOList) {
            // 查询配置项
            RoomConfigParamDO existRoomConfigParamDO = existRoomConfigParamMap.get(roomConfigParamBO.getCellConfigCode());
            // 原来为空就新增配置
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
}