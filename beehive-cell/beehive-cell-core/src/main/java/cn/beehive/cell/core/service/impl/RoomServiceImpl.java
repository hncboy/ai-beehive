package cn.beehive.cell.core.service.impl;

import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.base.mapper.RoomMapper;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.PageUtil;
import cn.beehive.cell.core.domain.bo.RoomConfigParamBO;
import cn.beehive.cell.core.domain.query.RoomPageQuery;
import cn.beehive.cell.core.domain.request.RoomCreateRequest;
import cn.beehive.cell.core.domain.request.RoomInfoEditRequest;
import cn.beehive.cell.core.domain.vo.RoomListVO;
import cn.beehive.cell.core.hander.CellPermissionHandler;
import cn.beehive.cell.core.hander.RoomConfigParamHandler;
import cn.beehive.cell.core.hander.RoomHandler;
import cn.beehive.cell.core.hander.converter.RoomConfigParamConverter;
import cn.beehive.cell.core.hander.converter.RoomConverter;
import cn.beehive.cell.core.service.RoomConfigParamService;
import cn.beehive.cell.core.service.RoomService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间业务接口实现类
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, RoomDO> implements RoomService {

    @Resource
    private RoomConfigParamService roomConfigParamService;

    @Override
    public IPage<RoomListVO> pageRoom(RoomPageQuery roomPageQuery) {
        Page<RoomDO> roomPage = page(new Page<>(roomPageQuery.getPageNum(), roomPageQuery.getPageSize()), new LambdaQueryWrapper<RoomDO>()
                // 自己的房间
                .eq(RoomDO::getUserId, FrontUserUtil.getUserId())
                // 名称模糊查询
                .like(StrUtil.isNotEmpty(roomPageQuery.getName()), RoomDO::getName, roomPageQuery.getName())
                // 未删除的
                .eq(RoomDO::getIsDeleted, false)
                // 根据固定时间降序
                .orderByDesc(RoomDO::getPinTime)
                // 根据主键降序
                .orderByDesc(RoomDO::getId));
        return PageUtil.toPage(roomPage, RoomConverter.INSTANCE.entityToListVO(roomPage.getRecords()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RoomListVO createRoom(RoomCreateRequest roomCreateRequest) {
        // 校验 Cell 是否有使用权限
        CellPermissionHandler.checkCanUse(roomCreateRequest.getCellCode());

        // 检查房间配置项参数请求
        List<RoomConfigParamBO> roomConfigParamBOList = RoomConfigParamHandler.checkRoomConfigParamRequest(roomCreateRequest.getCellCode(), roomCreateRequest.getRoomConfigParams(), false)
                // 过滤出用户自己填的
                .stream().filter(bo -> !bo.getIsUseDefaultValue()).toList();

        // 保存房间
        RoomDO roomDO = new RoomDO();
        roomDO.setUserId(FrontUserUtil.getUserId());
        roomDO.setColor(roomCreateRequest.getRoomInfo().getColor());
        roomDO.setName(roomCreateRequest.getRoomInfo().getName());
        roomDO.setPinTime(0L);
        roomDO.setCellCode(roomCreateRequest.getCellCode());
        roomDO.setIsDeleted(false);
        save(roomDO);

        // 保存房间配置参数
        List<RoomConfigParamDO> roomConfigParamDOList = roomConfigParamBOList.stream()
                .map(roomConfigParamBO -> RoomConfigParamConverter.INSTANCE.boToEntity(roomConfigParamBO, roomDO.getId()))
                .collect(Collectors.toList());
        roomConfigParamService.saveBatch(roomConfigParamDOList);

        return RoomConverter.INSTANCE.entityToListVO(roomDO);
    }

    @Override
    public RoomListVO pinRoom(Long roomId) {
        RoomDO roomDO = RoomHandler.checkRoomExist(roomId);
        roomDO.setPinTime(System.currentTimeMillis());
        updateById(roomDO);
        return RoomConverter.INSTANCE.entityToListVO(roomDO);
    }

    @Override
    public RoomListVO editRoom(RoomInfoEditRequest roomInfoEditRequest) {
        RoomDO roomDO = RoomHandler.checkRoomExist(roomInfoEditRequest.getRoomId());
        roomDO.setName(roomInfoEditRequest.getName());
        roomDO.setColor(roomInfoEditRequest.getColor());
        updateById(roomDO);
        return RoomConverter.INSTANCE.entityToListVO(roomDO);
    }

    @Override
    public void deleteRoom(Long roomId) {
        RoomDO roomDO = RoomHandler.checkRoomExist(roomId);
        roomDO.setIsDeleted(true);
        updateById(roomDO);
    }
}
