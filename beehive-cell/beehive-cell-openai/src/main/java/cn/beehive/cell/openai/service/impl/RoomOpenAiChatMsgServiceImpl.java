package cn.beehive.cell.openai.service.impl;

import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.handler.mp.BeehiveServiceImpl;
import cn.beehive.base.mapper.RoomOpenAiChatMsgMapper;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.base.hander.RoomHandler;
import cn.beehive.cell.base.hander.strategy.CellConfigFactory;
import cn.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import cn.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import cn.beehive.cell.openai.handler.converter.RoomOpenAiChatMsgConverter;
import cn.beehive.cell.openai.module.chat.emitter.RoomOpenAiChatResponseEmitter;
import cn.beehive.cell.openai.module.chat.emitter.RoomOpenAiChatResponseEmitterDispatcher;
import cn.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间消息服务层
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
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取房间信息
        Map<CellCodeEnum, RoomOpenAiChatResponseEmitter> responseEmitterMap = RoomOpenAiChatResponseEmitterDispatcher.RESPONSE_EMITTER_MAP;
        RoomDO roomDO = RoomHandler.checkRoomExist(sendRequest.getRoomId(), new ArrayList<>(responseEmitterMap.keySet()));

        // 转换为对应的响应处理器
        RoomOpenAiChatResponseEmitter responseEmitter = RoomOpenAiChatResponseEmitterDispatcher.doDispatch(roomDO.getCellCode());
        responseEmitter.requestToResponseEmitter(sendRequest, emitter, cellConfigFactory.getCellConfigStrategy(roomDO.getCellCode()));

        return emitter;
    }
}
