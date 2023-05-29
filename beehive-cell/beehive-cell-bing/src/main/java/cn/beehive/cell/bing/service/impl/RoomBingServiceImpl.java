package cn.beehive.cell.bing.service.impl;

import cn.beehive.base.domain.entity.RoomBingDO;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.mapper.RoomBingMapper;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.base.hander.RoomHandler;
import cn.beehive.cell.base.hander.strategy.DataWrapper;
import cn.beehive.cell.bing.domain.bo.BingApiCreateConversationResultBO;
import cn.beehive.cell.bing.domain.bo.BingRoomBO;
import cn.beehive.cell.bing.handler.BingCellConfigCodeEnum;
import cn.beehive.cell.bing.handler.BingCellConfigStrategy;
import cn.beehive.cell.bing.handler.BingRoomHandler;
import cn.beehive.cell.bing.service.RoomBingService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间业务接口实现类
 */
@Slf4j
@Service
public class RoomBingServiceImpl extends ServiceImpl<RoomBingMapper, RoomBingDO> implements RoomBingService {

    @Resource
    private BingCellConfigStrategy bingCellConfigStrategy;

    @Override
    public BingRoomBO getRoom(Long roomId, boolean isNewTopic) {
        RoomBingDO roomBingDO = getById(roomId);
        BingRoomBO bingRoomBO = new BingRoomBO();
        bingRoomBO.setRoomBingDO(roomBingDO);

        // 获取房间配置参数
        Map<BingCellConfigCodeEnum, DataWrapper> roomConfigParamMap = bingCellConfigStrategy.getRoomConfigParamAsMap(roomId);
        String mode = roomConfigParamMap.get(BingCellConfigCodeEnum.MODE).asString();

        if (Objects.isNull(roomBingDO)) {
            // 校验房间
            RoomHandler.checkRoomExist(roomId);

            roomBingDO = new RoomBingDO();
            roomBingDO.setRoomId(roomId);
            roomBingDO.setUserId(FrontUserUtil.getUserId());
            roomBingDO.setMode(mode);
            // 初始化 bing 对话
            initConversation(roomBingDO);
            save(roomBingDO);

            bingRoomBO.setIsNewTopic(true);
            bingRoomBO.setRefreshRoomReason("房间不存在初始化房间");
            return bingRoomBO;
        }

        if (!roomBingDO.getUserId().equals(FrontUserUtil.getUserId())) {
            throw new ServiceException("房间不存在");
        }

        // 开启新对话就要刷新房间信息
        if (isNewTopic) {
            bingRoomBO.setRefreshRoomReason("用户选择开启话题");
            return refreshRoom(bingRoomBO);
        }

        // mode 改变需要开启新主题
        if (ObjectUtil.notEqual(mode, roomBingDO.getMode())) {
            bingRoomBO.setRefreshRoomReason("房间模式改变");
            return refreshRoom(bingRoomBO);
        }

        bingRoomBO.setIsNewTopic(false);
        return bingRoomBO;
    }

    @Override
    public BingRoomBO refreshRoom(BingRoomBO bingRoomBO) {
        bingRoomBO.setIsNewTopic(true);

        // 初始化 bing 对话
        RoomBingDO roomBingDO = bingRoomBO.getRoomBingDO();
        initConversation(roomBingDO);
        updateById(roomBingDO);
        return bingRoomBO;
    }

    /**
     * 初始化房间对话信息
     *
     * @param roomBingDO 房间信息
     */
    private void initConversation(RoomBingDO roomBingDO) {
        BingApiCreateConversationResultBO bingConversation = BingRoomHandler.createConversation(roomBingDO.getRoomId());
        roomBingDO.setConversationId(bingConversation.getConversationId());
        roomBingDO.setClientId(bingConversation.getClientId());
        roomBingDO.setConversationSignature(bingConversation.getConversationSignature());
        roomBingDO.setMaxNumUserMessagesInConversation(0);
        roomBingDO.setNumUserMessagesInConversation(0);
    }
}