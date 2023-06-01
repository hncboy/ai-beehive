package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.domain.request.RoomConfigParamRequest;
import cn.beehive.cell.core.hander.strategy.CellConfigFactory;
import cn.beehive.cell.core.hander.strategy.CellConfigStrategy;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

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
     * @param cellCode                cellCode
     * @param roomConfigParamRequests 房间配置项参数请求实体列表
     * @param isEditRoom              是否是编辑房间
     * @return 房间配置项参数实体类列表
     */
    public static List<RoomConfigParamDO> checkRoomConfigParamRequest(CellCodeEnum cellCode, List<RoomConfigParamRequest> roomConfigParamRequests, boolean isEditRoom) {
        // 判断房间配置项参数 code 是否重复
        long roomConfigParamRequestDistinctCount = roomConfigParamRequests.stream().map(RoomConfigParamRequest::getCode).distinct().count();
        if (roomConfigParamRequestDistinctCount != roomConfigParamRequests.size()) {
            throw new ServiceException("房间配置项参数 code 重复");
        }

        // 校验一个个配置项
        // 将房间配置项参数请求转为 Map
        Map<String, RoomConfigParamRequest> roomConfigParamRequestMap = roomConfigParamRequests.stream()
                .collect(Collectors.toMap(RoomConfigParamRequest::getCode, Function.identity()));
        List<RoomConfigParamDO> roomConfigParamDOList = new ArrayList<>();

        // 获取 Cell 配置项权限列表
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigPermissionHandler.listCellConfigPermission(cellCode);

        // 获取 Cell 配置项策略
        CellConfigStrategy cellConfigStrategy = SpringUtil.getBean(CellConfigFactory.class).getCellConfigStrategy(cellCode);
        Map<String, ICellConfigCodeEnum> cellConfigCodeMap = cellConfigStrategy.getCellConfigCodeMap();

        // 校验配置是否齐全，是否都在对应枚举内，防止数据库未补充完整所有配置项
        List<String> cellConfigCodes = cellConfigPermissionBOList.stream().map(CellConfigPermissionBO::getCode).toList();
        for (String code : cellConfigCodeMap.keySet()) {
            if (!cellConfigCodes.contains(code)) {
                throw new ServiceException(StrUtil.format("该图纸存在配置项[{}]未配置，请联系管理员配置", code));
            }
        }

        // 遍历 Cell 配置项校验用户配置项参数
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            // 跳过用户不能修改的
            if (!cellConfigPermissionBO.getIsUserModifiable()) {
                continue;
            }

            // 跳过用户不可见的
            if (!cellConfigPermissionBO.getIsUserVisible()) {
                continue;
            }

            // 如果是编辑房间参数，跳过用户创建房间后不能修改的
            if (isEditRoom && !cellConfigPermissionBO.getIsUserLiveModifiable()) {
                continue;
            }

            // 获取用户传的该配置项参数
            RoomConfigParamRequest roomConfigParamRequest = roomConfigParamRequestMap.get(cellConfigPermissionBO.getCode());
            // 如果用户没有传该配置项参数 或 传了配置项但是使用默认值
            if (Objects.isNull(roomConfigParamRequest) || roomConfigParamRequest.getIsDefaultValue()) {
                // 如果是必填项并且无默认值，则抛出异常
                if (cellConfigPermissionBO.getIsRequired()) {
                    // 如果无默认值
                    if (!cellConfigPermissionBO.getIsHaveDefaultValue()) {
                        throw new ServiceException("无默认值，缺少必填项：" + cellConfigPermissionBO.getName());
                    }
                    // 如果有默认值用户无法使用
                    if (!cellConfigPermissionBO.getIsUserCanUseDefaultValue()) {
                        throw new ServiceException("用户需填写必填项：" + cellConfigPermissionBO.getName());
                    }
                }
                // 使用系统的默认值
                continue;
            }

            // 使用用户自己传的值，校验值是否合法
            cellConfigStrategy.validate(cellConfigCodeMap.get(cellConfigPermissionBO.getCode()), new DataWrapper(roomConfigParamRequest.getValue()));

            RoomConfigParamDO roomConfigParamDO = new RoomConfigParamDO();
            roomConfigParamDO.setUserId(FrontUserUtil.getUserId());
            roomConfigParamDO.setRoomId(null);
            roomConfigParamDO.setCellConfigCode(cellConfigPermissionBO.getCode());
            roomConfigParamDO.setValue(roomConfigParamRequest.getValue());
            roomConfigParamDO.setIsDeleted(false);
            roomConfigParamDOList.add(roomConfigParamDO);
        }

        // 传 Map 校验 TODO
        return roomConfigParamDOList;
    }
}
