package cn.beehive.cell.bing.service.impl;

import cn.beehive.base.domain.entity.RoomBingDO;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.mapper.RoomBingMapper;
import cn.beehive.base.util.ForestRequestUtil;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.bing.domain.bo.BingApiCreateConversationResultBO;
import cn.beehive.cell.bing.domain.bo.BingApiSendThrottlingResultBO;
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
    public RoomBingDO getRoom(Long roomId) {
        RoomBingDO roomBingDO = getById(roomId);

        if (Objects.isNull(roomBingDO)) {
            // TODO 查询通用房间，判断是否存在

            // 创建 bing 对话
            BingApiCreateConversationResultBO bingConversation = createConversation(roomId);

            roomBingDO = new RoomBingDO();
            roomBingDO.setRoomId(roomId);
            roomBingDO.setUserId(FrontUserUtil.getUserId());
            roomBingDO.setConversationId(bingConversation.getConversationId());
            roomBingDO.setClientId(bingConversation.getClientId());
            roomBingDO.setConversationSignature(bingConversation.getConversationSignature());
            roomBingDO.setMaxNumUserMessagesInConversation(0);
            roomBingDO.setNumUserMessagesInConversation(0);
            save(roomBingDO);
            return roomBingDO;
        }

        if (roomBingDO.getUserId().equals(FrontUserUtil.getUserId())) {
            return roomBingDO;
        }

        throw new ServiceException("房间不存在");
    }

    @Override
    public RoomBingDO refreshRoom(RoomBingDO roomBingDO) {
        // 创建 bing 对话
        BingApiCreateConversationResultBO bingConversation = createConversation(roomBingDO.getRoomId());

        roomBingDO.setConversationId(bingConversation.getConversationId());
        roomBingDO.setClientId(bingConversation.getClientId());
        roomBingDO.setConversationSignature(bingConversation.getConversationSignature());
        roomBingDO.setMaxNumUserMessagesInConversation(0);
        roomBingDO.setNumUserMessagesInConversation(0);
        updateById(roomBingDO);
        return roomBingDO;
    }

    @Override
    public void updateRoomMessageNum(RoomBingDO roomBingDO, BingApiSendThrottlingResultBO throttling) {
        roomBingDO.setMaxNumUserMessagesInConversation(throttling.getMaxNumUserMessagesInConversation());
        roomBingDO.setNumUserMessagesInConversation(throttling.getNumUserMessagesInConversation());
        updateById(roomBingDO);
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