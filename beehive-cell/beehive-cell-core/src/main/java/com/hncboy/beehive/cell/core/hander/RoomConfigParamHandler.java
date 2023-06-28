package com.hncboy.beehive.cell.core.hander;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.domain.request.RoomConfigParamRequest;
import com.hncboy.beehive.cell.core.hander.converter.RoomConfigParamConverter;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
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
     * 检查房间配置项参数请求并返回结果
     *
     * @param cellCode                cellCode
     * @param roomConfigParamRequests 房间配置项参数请求实体列表
     * @param isEditRoom              是否是编辑房间
     * @return 房间配置项参数业务对象列表
     */
    public static List<RoomConfigParamBO> checkRoomConfigParamRequest(CellCodeEnum cellCode, List<RoomConfigParamRequest> roomConfigParamRequests, boolean isEditRoom) {
        // 判断房间配置项参数 code 是否重复
        long roomConfigParamRequestDistinctCount = roomConfigParamRequests.stream().map(RoomConfigParamRequest::getCellConfigCode).distinct().count();
        if (roomConfigParamRequestDistinctCount != roomConfigParamRequests.size()) {
            throw new ServiceException("房间配置项参数 code 重复");
        }

        // 获取 Cell 配置项权限业务对象列表
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigPermissionHandler.listCellConfigPermission(cellCode);

        // 获取 Cell 配置项策略
        CellConfigStrategy cellConfigStrategy = SpringUtil.getBean(CellConfigFactory.class).getCellConfigStrategy(cellCode);
        Map<String, ICellConfigCodeEnum> cellConfigCodeMap = cellConfigStrategy.getCellConfigCodeMap();

        // 校验用户填写的配置项是否都存在
        roomConfigParamRequests.forEach(roomConfigParamRequest -> {
            if (!cellConfigCodeMap.containsKey(roomConfigParamRequest.getCellConfigCode())) {
                throw new ServiceException(StrUtil.format("该图纸不存在配置项[{}]", roomConfigParamRequest.getCellConfigCode()));
            }
        });

        // 校验配置是否齐全，是否都在对应枚举内，避免数据库未补充完整所有配置项导致创建房间失败
        List<String> cellConfigCodes = cellConfigPermissionBOList.stream().map(CellConfigPermissionBO::getCellConfigCode).toList();
        for (String code : cellConfigCodeMap.keySet()) {
            if (!cellConfigCodes.contains(code)) {
                throw new ServiceException(StrUtil.format("该图纸存在配置项[{}]未配置，请联系管理员配置", code));
            }
        }

        // 填充房间配置项参数业务对象
        List<RoomConfigParamBO> roomConfigParamBOList = populateRoomConfigParamBO(cellConfigPermissionBOList, roomConfigParamRequests, isEditRoom);

        // 复合校验房间配置项参数
        cellConfigStrategy.compositeValidate(roomConfigParamBOList);

        return roomConfigParamBOList;
    }

    /**
     * 填充房间配置项参数业务对象
     *
     * @param cellConfigPermissionBOList Cell 配置项权限列表
     * @param roomConfigParamRequests    房间配置项参数请求参数列表
     * @param isEditRoom                 是否是编辑房间
     * @return 房间配置项参数业务对象列表
     */
    private static List<RoomConfigParamBO> populateRoomConfigParamBO(List<CellConfigPermissionBO> cellConfigPermissionBOList, List<RoomConfigParamRequest> roomConfigParamRequests, boolean isEditRoom) {
        // 将房间配置项参数请求转为 Map
        Map<String, RoomConfigParamRequest> roomConfigParamRequestMap = roomConfigParamRequests.stream()
                .collect(Collectors.toMap(RoomConfigParamRequest::getCellConfigCode, Function.identity()));

        List<RoomConfigParamBO> roomConfigParamBOList = new ArrayList<>();

        // 遍历 Cell 配置项校验用户配置项参数
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            // 获取用户传的该配置项参数
            RoomConfigParamRequest roomConfigParamRequest = roomConfigParamRequestMap.get(cellConfigPermissionBO.getCellConfigCode());

            // 转换为房间配置项参数业务对象
            RoomConfigParamBO roomConfigParamBO = RoomConfigParamConverter.INSTANCE.cellConfigPermissionBoToBo(cellConfigPermissionBO);

            // 是否跳过房间配置项
            if (isSkipRoomConfig(cellConfigPermissionBO, roomConfigParamRequest, isEditRoom)) {
                roomConfigParamBO.setIsUseDefaultValue(true);
                roomConfigParamBO.setValue(cellConfigPermissionBO.getDefaultValue());
            } else {
                // 不使用默认值，使用用户填的
                roomConfigParamBO.setIsUseDefaultValue(false);
                roomConfigParamBO.setValue(roomConfigParamRequest.getValue());
            }

            roomConfigParamBOList.add(roomConfigParamBO);
        }

        return roomConfigParamBOList;
    }

    /**
     * 判断是否跳过房间配置项
     *
     * @param cellConfigPermissionBO 配置项权限
     * @param roomConfigParamRequest 房间配置项参数请求
     * @param isEditRoom             是否是编辑房间
     * @return 是否跳过
     */
    private static boolean isSkipRoomConfig(CellConfigPermissionBO cellConfigPermissionBO, RoomConfigParamRequest roomConfigParamRequest, boolean isEditRoom) {
        // 跳过用户不能修改的
        if (!cellConfigPermissionBO.getIsUserModifiable()) {
            return true;
        }

        // 跳过用户不可见的
        if (!cellConfigPermissionBO.getIsUserVisible()) {
            return true;
        }

        // 如果是编辑房间参数，跳过用户创建房间后不能修改的
        if (isEditRoom && !cellConfigPermissionBO.getIsUserLiveModifiable()) {
            return true;
        }

        // 如果用户没有传该配置项参数 或 传了配置项但是使用默认值
        if (Objects.isNull(roomConfigParamRequest) || roomConfigParamRequest.getIsUseDefaultValue()) {
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
            return true;
        }

        return false;
    }
}
