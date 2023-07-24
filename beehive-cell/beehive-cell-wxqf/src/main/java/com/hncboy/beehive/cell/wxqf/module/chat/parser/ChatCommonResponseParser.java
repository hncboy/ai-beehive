package com.hncboy.beehive.cell.wxqf.module.chat.parser;

import cn.hutool.json.JSONUtil;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import com.hncboy.beehive.cell.wxqf.module.chat.storage.RoomWxqfChatMessageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/24
 * 对话通用应解析器
 */
@Slf4j
@Component
public class ChatCommonResponseParser implements ResponseParser<WxqfChatApiCommonResponse> {

    @Override
    public WxqfChatApiCommonResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, WxqfChatApiCommonResponse.class);
    }

    @Override
    public String parseReceivedMessage(String receivedMessage, String newMessage) {
        return receivedMessage.concat(newMessage);
    }

    @Override
    public String parseNewMessage(String originalData) {
        return parseSuccess(originalData).getResult();
    }

    @Override
    public RoomWxqfChatMsgVO parseChatMessageVO(RoomWxqfChatMessageStorage chatMessageStorage) {
        RoomWxqfChatMsgDO answerMessage = chatMessageStorage.getAnswerMessageDO();
        if (Objects.isNull(answerMessage)) {
            return null;
        }
        RoomWxqfChatMsgVO roomWxqfChatMsgVO = new RoomWxqfChatMsgVO();
        roomWxqfChatMsgVO.setId(answerMessage.getId());
        roomWxqfChatMsgVO.setContent(chatMessageStorage.getReceivedMessage());
        roomWxqfChatMsgVO.setMessageType(MessageTypeEnum.ANSWER);
        roomWxqfChatMsgVO.setCreateTime(answerMessage.getCreateTime());
        return roomWxqfChatMsgVO;
    }

    @Override
    public String parseErrorMessage(String originalResponseErrorMsg) {
        // 首先得满足 json 格式
        if (!JSONUtil.isTypeJSON(originalResponseErrorMsg)) {
            return "网络故障，请稍后再试";
        }

        // 详细错误 TODO

        return "不好意思报错了，请稍后再试，如果错误次数过多请联系管理员";
    }
}
