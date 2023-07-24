package com.hncboy.beehive.cell.wxqf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.mapper.RoomWxqfChatMsgMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.handler.converter.RoomWxqfChatMsgConverter;
import com.hncboy.beehive.cell.wxqf.module.chat.emitter.AbstractWxqfChatResponseEmitter;
import com.hncboy.beehive.cell.wxqf.module.chat.emitter.WxqfChatResponseEmitterDispatcher;
import com.hncboy.beehive.cell.wxqf.service.RoomWxqfChatMsgService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息业务实现类
 */
@Slf4j
@Service
public class RoomWxqfChatMsgServiceImpl extends BeehiveServiceImpl<RoomWxqfChatMsgMapper, RoomWxqfChatMsgDO> implements RoomWxqfChatMsgService {

    @Resource
    private CellConfigFactory cellConfigFactory;

    @Override
    public List<RoomWxqfChatMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomWxqfChatMsgDO> cursorList = cursorList(cursorQuery, RoomWxqfChatMsgDO::getId, new LambdaQueryWrapper<RoomWxqfChatMsgDO>()
                .eq(RoomWxqfChatMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomWxqfChatMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomWxqfChatMsgConverter.INSTANCE.entityToVO(cursorList);
    }

    @Override
    public ResponseBodyEmitter send(RoomWxqfChatSendRequest sendRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("文心千帆对话请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("文心千帆对话请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取房间信息
        Map<CellCodeEnum, AbstractWxqfChatResponseEmitter> responseEmitterMap = WxqfChatResponseEmitterDispatcher.RESPONSE_EMITTER_MAP;
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(sendRequest.getRoomId(), new ArrayList<>(responseEmitterMap.keySet()));

        // 转换为对应的响应处理器
        AbstractWxqfChatResponseEmitter responseEmitter = WxqfChatResponseEmitterDispatcher.doDispatch(roomDO.getCellCode());
        responseEmitter.requestToResponseEmitter(sendRequest, emitter, cellConfigFactory.getCellConfigStrategy(roomDO.getCellCode()));

        return emitter;
    }
}
