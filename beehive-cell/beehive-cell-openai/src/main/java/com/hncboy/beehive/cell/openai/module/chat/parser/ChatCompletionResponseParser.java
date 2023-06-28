package com.hncboy.beehive.cell.openai.module.chat.parser;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.module.chat.storage.RoomOpenAiChatMessageStorage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023-3-24
 * ApiKey 的 ChatCompletion 响应解析器
 */
@Slf4j
@Component
public class ChatCompletionResponseParser implements ResponseParser<ChatCompletionResponse> {

    @Override
    public ChatCompletionResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, ChatCompletionResponse.class);
    }

    @Override
    public String parseReceivedMessage(String receivedMessage, String newMessage) {
        return receivedMessage.concat(newMessage);
    }

    @Override
    public String parseNewMessage(String originalData) {
        Message message = getMessage(originalData);
        if (Objects.isNull(message)) {
            return null;
        }
        return message.getContent();
    }

    @Override
    public RoomOpenAiChatMsgVO parseChatMessageVO(RoomOpenAiChatMessageStorage chatMessageStorage) {
        if (Objects.isNull(chatMessageStorage.getAnswerMessageDO())) {
            return null;
        }
        RoomOpenAiChatMsgVO roomOpenAiChatMsgVO = new RoomOpenAiChatMsgVO();
        RoomOpenAiChatMsgDO answerMessage = (RoomOpenAiChatMsgDO) chatMessageStorage.getAnswerMessageDO();
        roomOpenAiChatMsgVO.setId(answerMessage.getId());
        roomOpenAiChatMsgVO.setContent(chatMessageStorage.getReceivedMessage());
        roomOpenAiChatMsgVO.setMessageType(MessageTypeEnum.ANSWER);
        roomOpenAiChatMsgVO.setCreateTime(answerMessage.getCreateTime());
        return roomOpenAiChatMsgVO;
    }

    @Override
    public String parseErrorMessage(String originalResponseErrorMsg) {
        try {
            // 首先得满足 json 格式，有些情况比如网页直接 502 非接口返回的就是 html，也有可能是网络故障
            if (!JSONUtil.isTypeJSON(originalResponseErrorMsg)) {
                return "网络故障，请稍后再试";
            }

            // 提取 code
            JsonNode errorJsonNode = ObjectMapperUtil.readTree(originalResponseErrorMsg).get("error");
            String code = errorJsonNode.get("code").asText();
            String type = errorJsonNode.get("type").asText();

            // 先通过 code，再通过 type
            ChatCompletionErrorCodeEnum chatCompletionErrorCodeEnum = Optional.ofNullable(code)
                    .map(ChatCompletionErrorCodeEnum.CODE_MAP::get)
                    .orElseGet(() -> Optional.ofNullable(type)
                            .map(ChatCompletionErrorCodeEnum.CODE_MAP::get)
                            .orElse(null));

            if (Objects.isNull(chatCompletionErrorCodeEnum)) {
                return "未知编码错误，请稍后再试";
            }
            return chatCompletionErrorCodeEnum.getMessage();
        } catch (Exception e) {
            log.error("OpenAi ApiKey 解析错误信息失败，错误信息：{}", originalResponseErrorMsg, e);
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
