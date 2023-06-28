package com.hncboy.beehive.cell.bing.service.impl;

import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.mapper.RoomBingMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiCreateConversationResultBO;
import com.hncboy.beehive.cell.bing.domain.bo.BingRoomBO;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.hncboy.beehive.cell.bing.enums.BingCellConfigCodeEnum;
import com.hncboy.beehive.cell.bing.enums.BingModeEnum;
import com.hncboy.beehive.cell.bing.handler.BingCellConfigStrategy;
import com.hncboy.beehive.cell.bing.handler.BingRoomHandler;
import com.hncboy.beehive.cell.bing.service.RoomBingService;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
    public BingRoomBO getRoom(Long roomId, RoomBingMsgSendRequest sendRequest) {
        RoomHandler.checkRoomExistAndCellCanUse(roomId, CellCodeEnum.NEW_BING);

        // 获取房间配置参数
        Map<BingCellConfigCodeEnum, DataWrapper> roomConfigParamMap = bingCellConfigStrategy.getRoomConfigParamAsMap(roomId);
        String mode = roomConfigParamMap.get(BingCellConfigCodeEnum.MODE).asString();
        // 根据不同模式校验字数
        Integer limitWords = BingModeEnum.NAME_MAP.get(mode).getLimitWords();
        if (limitWords <= sendRequest.getContent().length()) {
            throw new ServiceException(StrUtil.format("字数超过限制 {} 字", limitWords));
        }

        RoomBingDO roomBingDO = getById(roomId);
        BingRoomBO bingRoomBO = new BingRoomBO();
        bingRoomBO.setRoomBingDO(roomBingDO);
        if (Objects.isNull(roomBingDO)) {
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
        if (sendRequest.getIsNewTopic()) {
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