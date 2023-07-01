package com.hncboy.beehive.cell.openai.service.impl;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.mapper.RoomOpenAiChatWebMsgMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatWebCellConfigCodeEnum;
import com.hncboy.beehive.cell.openai.handler.converter.RoomOpenAiChatWebMsgConverter;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatWebMsgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author hncboy
 * @date 2023/6/1
 * OpenAi 对话 Web 房间消息业务实现类
 */
@Service
public class RoomOpenAiChatWebMsgServiceImpl extends BeehiveServiceImpl<RoomOpenAiChatWebMsgMapper, RoomOpenAiChatWebMsgDO> implements RoomOpenAiChatWebMsgService {

    @Override
    public List<RoomOpenAiChatMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomOpenAiChatWebMsgDO> cursorList = cursorList(cursorQuery, RoomOpenAiChatWebMsgDO::getId, new LambdaQueryWrapper<RoomOpenAiChatWebMsgDO>()
                .eq(RoomOpenAiChatWebMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomOpenAiChatWebMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomOpenAiChatWebMsgConverter.INSTANCE.entityToVO(cursorList);
    }

    @Override
    public RoomOpenAiChatWebMsgDO initQuestionMessage(RoomOpenAiChatSendRequest sendRequest, Map<OpenAiChatWebCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        // 按照 id 倒序排序，取该房间最新的一条回答消息
        RoomOpenAiChatWebMsgDO latestAnswerMessage = getOne(new LambdaQueryWrapper<RoomOpenAiChatWebMsgDO>()
                .eq(RoomOpenAiChatWebMsgDO::getRoomId, sendRequest.getRoomId())
                // 部分成功和全部成功都算
                .in(RoomOpenAiChatWebMsgDO::getStatus, RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS, RoomOpenAiChatMsgStatusEnum.PART_SUCCESS)
                .eq(RoomOpenAiChatWebMsgDO::getMessageType, MessageTypeEnum.ANSWER)
                .last("limit 1")
                .orderByDesc(RoomOpenAiChatWebMsgDO::getId));

        RoomOpenAiChatWebMsgDO questionMessage = new RoomOpenAiChatWebMsgDO();
        questionMessage.setId(IdWorker.getId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setRequestMessageId(UUID.randomUUID().toString());
        questionMessage.setRequestConversationId(Optional.ofNullable(latestAnswerMessage)
                .map(RoomOpenAiChatWebMsgDO::getRequestConversationId)
                .orElse(null));
        // 请求 parentMessageId 为空的话随机生成一个
        questionMessage.setRequestParentMessageId(Optional.ofNullable(latestAnswerMessage)
                .map(RoomOpenAiChatWebMsgDO::getRequestMessageId)
                .orElse(UUID.randomUUID().toString()));
        questionMessage.setRoomId(sendRequest.getRoomId());
        questionMessage.setIp(WebUtil.getIp());
        questionMessage.setMessageType(MessageTypeEnum.QUESTION);
        questionMessage.setModelName(roomConfigParamAsMap.get(OpenAiChatWebCellConfigCodeEnum.MODEL).asString());
        questionMessage.setContent(sendRequest.getContent());
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.INIT);
        return questionMessage;
    }
}
