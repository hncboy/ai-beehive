package com.hncboy.beehive.cell.chatglm.module.chat.parser;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import com.hncboy.beehive.cell.chatglm.module.chat.storage.RoomChatGlmMessageStorage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * ApiKey 的 ChatCompletion 响应解析器
 */
@Slf4j
@Component
public class ChatGlmCompletionResponseParser{

    public ChatCompletionResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, ChatCompletionResponse.class);
    }

    public String parseReceivedMessage(String receivedMessage, String newMessage) {
        return receivedMessage.concat(newMessage);
    }

    public String parseNewMessage(String originalData) {
        Message message = getMessage(originalData);
        if (Objects.isNull(message)) {
            return null;
        }
        return message.getContent();
    }

    public RoomChatGlmChatMsgVO parseChatMessageVO(RoomChatGlmMessageStorage chatMessageStorage) {
        if (Objects.isNull(chatMessageStorage.getAnswerMessageDO())) {
            return null;
        }
        RoomChatGlmChatMsgVO roomChatGLMMsgVO = new RoomChatGlmChatMsgVO();
        RoomChatGlmMsgDO answerMessage = (RoomChatGlmMsgDO) chatMessageStorage.getAnswerMessageDO();
        roomChatGLMMsgVO.setId(answerMessage.getId());
        roomChatGLMMsgVO.setContent(chatMessageStorage.getReceivedMessage());
        roomChatGLMMsgVO.setMessageType(MessageTypeEnum.ANSWER);
        roomChatGLMMsgVO.setCreateTime(answerMessage.getCreateTime());
        return roomChatGLMMsgVO;
    }

    public String parseErrorMessage(String originalResponseErrorMsg) {
        try {
            // 首先得满足 json 格式，有些情况比如网页直接 502 非接口返回的就是 html，也有可能是网络故障
            if (!JSONUtil.isTypeJSON(originalResponseErrorMsg)) {
                return "网络故障，请稍后再试";
            }
            return "未知编码错误，请稍后再试";
        } catch (Exception e) {
            log.error("ChatGLM 解析错误信息失败，错误信息：{}", originalResponseErrorMsg, e);
            return "未知解析错误，请稍后再试";
        }
    }

    /**
     * 获取消息
     *
     * @param originalData 原始数据
     * @return 消息
     */
    private Message getMessage(String originalData) {
        ChatCompletionResponse response = parseSuccess(originalData);
        List<ChatChoice> choices = response.getChoices();
        if (CollectionUtil.isEmpty(choices)) {
            return null;
        }
        return choices.get(0).getDelta();
    }
}
