package cn.beehive.cell.base.hander;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.base.domain.request.RoomConfigParamRequest;
import cn.beehive.cell.base.service.CellConfigService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间配置项参数处理类
 */
public class RoomConfigParamHandler {

    /**
     * 校验房间配置项参数请求
     *
     * @param cellId                  cellId
     * @param roomConfigParamRequests 房间配置项参数请求实体列表
     * @param isEditRoom              是否是编辑房间
     * @return 房间配置项参数实体类列表
     */
    public static List<RoomConfigParamDO> checkRoomConfigParamRequest(Integer cellId, List<RoomConfigParamRequest> roomConfigParamRequests, boolean isEditRoom) {
        // 判断房间配置项参数 code 是否重复
        long roomConfigParamRequestDistinctCount = roomConfigParamRequests.stream().map(RoomConfigParamRequest::getCode).distinct().count();
        if (roomConfigParamRequestDistinctCount != roomConfigParamRequests.size()) {
            throw new ServiceException("房间配置项参数 code 重复");
        }

        // 获取 Cell 配置项列表
        List<CellConfigDO> cellConfigDOList = SpringUtil.getBean(CellConfigService.class)
                .list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellId, cellId));

        // 校验一个个配置项
        // 将房间配置项参数请求转为 Map
        Map<String, RoomConfigParamRequest> roomConfigParamRequestMap = roomConfigParamRequests.stream()
                .collect(Collectors.toMap(RoomConfigParamRequest::getCode, Function.identity()));
        List<RoomConfigParamDO> roomConfigParamDOList = new ArrayList<>();
        // 遍历 Cell 配置项
        for (CellConfigDO cellConfigDO : cellConfigDOList) {
            // 跳过用户不能修改的
            if (!cellConfigDO.getIsUserModifiable()) {
                continue;
            }

            // 跳过用户不可见的
            if (!cellConfigDO.getIsUserVisible()) {
                continue;
            }

            // 跳过用户创建房间后不能修改的
            if (isEditRoom && !cellConfigDO.getIsUserLiveModifiable()) {
                continue;
            }

            RoomConfigParamRequest roomConfigParamRequest = roomConfigParamRequestMap.get(cellConfigDO.getCode());
            // 如果用户没有传该配置项参数
            if (Objects.isNull(roomConfigParamRequest)) {
                // 如果是必填项并且默认值为空，则抛出异常
                if (cellConfigDO.getIsRequired() && StrUtil.isBlank(cellConfigDO.getDefaultValue())) {
                    throw new ServiceException("缺少必填项：" + cellConfigDO.getName());
                }
                continue;
            }

            // 用户传了该配置项参数但是使用默认值
            if (!roomConfigParamRequest.getIsDefaultValue()) {
                // 如果是必填项并且默认值为空，则抛出异常
                if (cellConfigDO.getIsRequired() && StrUtil.isBlank(cellConfigDO.getDefaultValue())) {
                    throw new ServiceException(StrUtil.format("必填项：{} 缺少值" + cellConfigDO.getName()));
                }
                continue;
            }

            // 用户传了该配置项参数但是未使用默认值
            // 如果是必填项并且默认值为空，则抛出异常
            if (cellConfigDO.getIsRequired() && StrUtil.isBlank(cellConfigDO.getDefaultValue())) {
                throw new ServiceException(StrUtil.format("必填项：{} 缺少值" + cellConfigDO.getName()));
            }

            // TODO 将 cell_code 和 cell_config_code 传到各自的子类进行各自的校验

            RoomConfigParamDO roomConfigParamDO = new RoomConfigParamDO();
            roomConfigParamDO.setUserId(FrontUserUtil.getUserId());
            roomConfigParamDO.setRoomId(null);
            roomConfigParamDO.setCellConfigCode(cellConfigDO.getCode());
            roomConfigParamDO.setValue(roomConfigParamRequest.getValue());
            roomConfigParamDO.setIsDeleted(false);
            roomConfigParamDOList.add(roomConfigParamDO);
        }

        return roomConfigParamDOList;
    }
}
