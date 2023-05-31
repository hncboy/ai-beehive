package cn.beehive.cell.base.hander;

import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.base.service.RoomService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间相关处理
 */
public class RoomHandler {

    /**
     * 校验房间是否并返回
     *
     * @param roomId 房间 id
     * @return 房间信息
     */
    public static RoomDO checkRoomExist(Long roomId) {
        RoomService roomService = SpringUtil.getBean(RoomService.class);
        RoomDO roomDO = roomService.getById(roomId);
        if (Objects.isNull(roomDO)) {
            throw new ServiceException("房间不存在");
        }
        // 校验房间是否属于当前用户
        if (ObjectUtil.notEqual(roomDO.getUserId(), FrontUserUtil.getUserId())) {
            throw new ServiceException("房间不存在");
        }
        if (roomDO.getIsDeleted()) {
            throw new ServiceException("房间已删除");
        }

        // 校验图纸是否存在并可发布
        CellHandler.checkCellPublishExist(roomDO.getCellCode());
        return roomDO;
    }

    /**
     * 校验房间是否存在并且属于指定的 cell code
     *
     * @param roomId       房间 id
     * @param cellCodeEnum 房间 cell Code
     * @return 房间信息
     */
    public static RoomDO checkRoomExist(Long roomId, CellCodeEnum cellCodeEnum) {
        return checkRoomExist(roomId, Collections.singletonList(cellCodeEnum));
    }

    /**
     * 校验房间是否存在并且属于指定的 cell code
     *
     * @param roomId        房间 id
     * @param cellCodeEnums 房间 cell Code 列表
     * @return 房间信息
     */
    public static RoomDO checkRoomExist(Long roomId, List<CellCodeEnum> cellCodeEnums) {
        RoomDO roomDO = checkRoomExist(roomId);
        // 校验房间 cell code
        if (!cellCodeEnums.contains(roomDO.getCellCode())) {
            throw new ServiceException("房间不存在");
        }
        return roomDO;
    }
}
