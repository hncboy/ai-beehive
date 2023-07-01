package com.hncboy.beehive.cell.openai.service.impl;

import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.mapper.RoomOpenAiChatMsgMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.handler.converter.RoomOpenAiChatMsgConverter;
import com.hncboy.beehive.cell.openai.module.chat.emitter.RoomOpenAiChatResponseEmitter;
import com.hncboy.beehive.cell.openai.module.chat.emitter.RoomOpenAiChatResponseEmitterDispatcher;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy·
 * @date 2023/5/31
 * OpenAi 对话房间消息业务实现类
 */
@Slf4j
@Service
public class RoomOpenAiChatMsgServiceImpl extends BeehiveServiceImpl<RoomOpenAiChatMsgMapper, RoomOpenAiChatMsgDO> implements RoomOpenAiChatMsgService {

    @Resource
    private CellConfigFactory cellConfigFactory;

    @Override
    public List<RoomOpenAiChatMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomOpenAiChatMsgDO> cursorList = cursorList(cursorQuery, RoomOpenAiChatMsgDO::getId, new LambdaQueryWrapper<RoomOpenAiChatMsgDO>()
                .eq(RoomOpenAiChatMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomOpenAiChatMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomOpenAiChatMsgConverter.INSTANCE.entityToVO(cursorList);
    }

    @Override
    public ResponseBodyEmitter send(RoomOpenAiChatSendRequest sendRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("OpenAi Chat 请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("OpenAi Chat 请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取房间信息
        Map<CellCodeEnum, RoomOpenAiChatResponseEmitter> responseEmitterMap = RoomOpenAiChatResponseEmitterDispatcher.RESPONSE_EMITTER_MAP;
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(sendRequest.getRoomId(), new ArrayList<>(responseEmitterMap.keySet()));

        // 转换为对应的响应处理器
        RoomOpenAiChatResponseEmitter responseEmitter = RoomOpenAiChatResponseEmitterDispatcher.doDispatch(roomDO.getCellCode());
        responseEmitter.requestToResponseEmitter(sendRequest, emitter, cellConfigFactory.getCellConfigStrategy(roomDO.getCellCode()));

        return emitter;
    }
}
