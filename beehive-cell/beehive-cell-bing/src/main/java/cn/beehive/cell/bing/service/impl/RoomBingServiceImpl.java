package cn.beehive.cell.bing.service.impl;

import cn.beehive.base.domain.entity.RoomBingDO;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.mapper.RoomBingMapper;
import cn.beehive.base.util.ForestRequestUtil;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.bing.domain.bo.BingApiCreateConversationResultBO;
import cn.beehive.cell.bing.domain.bo.BingApiSendThrottlingResultBO;
import cn.beehive.cell.bing.domain.bo.BingRoomBO;
import cn.beehive.cell.bing.service.RoomBingService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间业务接口实现类
 */
@Slf4j
@Service
public class RoomBingServiceImpl extends ServiceImpl<RoomBingMapper, RoomBingDO> implements RoomBingService {

    @Override
    public BingRoomBO getRoom(Long roomId, boolean isNewTopic) {
        RoomBingDO roomBingDO = getById(roomId);
        BingRoomBO bingRoomBO = new BingRoomBO();
        bingRoomBO.setRoomBingDO(roomBingDO);

        if (Objects.isNull(roomBingDO)) {
            // TODO 查询通用房间，判断是否存在，不存在报错

            roomBingDO = new RoomBingDO();
            roomBingDO.setRoomId(roomId);
            roomBingDO.setUserId(FrontUserUtil.getUserId());
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

        // TODO 判断模式是否改变，改变的情况自动开启新话题

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
        BingApiCreateConversationResultBO bingConversation = createConversation(roomBingDO.getRoomId());
        roomBingDO.setConversationId(bingConversation.getConversationId());
        roomBingDO.setClientId(bingConversation.getClientId());
        roomBingDO.setConversationSignature(bingConversation.getConversationSignature());
        roomBingDO.setMaxNumUserMessagesInConversation(0);
        roomBingDO.setNumUserMessagesInConversation(0);
    }

    /**
     * 创建 bing 对话
     *
     * @param roomId 放假 id
     * @return 创建结果
     */
    private BingApiCreateConversationResultBO createConversation(Long roomId) {
        ForestRequest<?> forestRequest = Forest.get("https://www.bing.com/turing/conversation/create");
        ForestRequestUtil.buildProxy(forestRequest);
        ForestResponse<?> forestResponse = forestRequest.execute(ForestResponse.class);
        if (forestResponse.isError()) {
            log.warn("用户 {} 房间 {} 创建 NewBing 会话失败，响应结果：{}", FrontUserUtil.getUserId(), roomId, forestResponse.getContent(), forestResponse.getException());
            throw new ServiceException("创建 NewBing 会话失败，请稍后再试");
        }

        BingApiCreateConversationResultBO resultBO = ObjectMapperUtil.fromJson(forestResponse.getContent(), BingApiCreateConversationResultBO.class);
        if (ObjectUtil.notEqual(resultBO.getResult().getValue(), "Success")) {
            // 有时候会报 {"result":{"value":"UnauthorizedRequest","message":"Sorry, you need to login first to access this service."}} 此时可以让用户多试几次
            log.warn("用户 {} 房间 {} 创建 NewBing 会话异常，响应结果：{}", FrontUserUtil.getUserId(), roomId, forestResponse.getContent());
            throw new ServiceException("创建 NewBing 会话异常，请稍后再试");
        }
        return resultBO;
    }
}