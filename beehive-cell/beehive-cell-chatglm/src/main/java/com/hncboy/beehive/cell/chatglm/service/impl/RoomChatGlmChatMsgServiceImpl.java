package com.hncboy.beehive.cell.chatglm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.mapper.RoomChatGlmMsgMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import com.hncboy.beehive.cell.chatglm.handler.converter.RoomChatGlmMsgConverter;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.chatglm.domain.request.RoomChatGlmSendRequest;
import com.hncboy.beehive.cell.chatglm.module.chat.emitter.RoomChatGlmChatApiResponseEmitter;
import com.hncboy.beehive.cell.chatglm.module.chat.emitter.RoomChatGlmChatResponseEmitterDispatcher;
import com.hncboy.beehive.cell.chatglm.service.RoomChatGlmMsgService;
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
 * ChatGlm 对话房间消息业务实现类
 */
@Slf4j
@Service
public class RoomChatGlmChatMsgServiceImpl extends BeehiveServiceImpl<RoomChatGlmMsgMapper, RoomChatGlmMsgDO> implements RoomChatGlmMsgService {

    @Resource
    private CellConfigFactory cellConfigFactory;

    @Override
    public List<RoomChatGlmChatMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomChatGlmMsgDO> cursorList = cursorList(cursorQuery, RoomChatGlmMsgDO::getId, new LambdaQueryWrapper<RoomChatGlmMsgDO>()
                .eq(RoomChatGlmMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomChatGlmMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomChatGlmMsgConverter.INSTANCE.entityToVO(cursorList);
    }

    @Override
    public ResponseBodyEmitter send(RoomChatGlmSendRequest sendRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("ChatGlm Chat 请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("ChatGlm Chat 请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取房间信息
        Map<CellCodeEnum, RoomChatGlmChatApiResponseEmitter> responseEmitterMap = RoomChatGlmChatResponseEmitterDispatcher.RESPONSE_EMITTER_MAP;
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(sendRequest.getRoomId(), new ArrayList<>(responseEmitterMap.keySet()));

        // 转换为对应的响应处理器
        RoomChatGlmChatApiResponseEmitter responseEmitter = RoomChatGlmChatResponseEmitterDispatcher.doDispatch(roomDO.getCellCode());
        responseEmitter.requestToResponseEmitter(sendRequest, emitter, cellConfigFactory.getCellConfigStrategy(roomDO.getCellCode()));

        return emitter;
    }
}
