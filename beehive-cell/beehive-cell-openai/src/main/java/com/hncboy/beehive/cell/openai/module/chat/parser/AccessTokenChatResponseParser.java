package com.hncboy.beehive.cell.openai.module.chat.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.module.chat.accesstoken.ChatWebConversationResponse;
import com.hncboy.beehive.cell.openai.module.chat.storage.RoomOpenAiChatMessageStorage;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023-3-24
 * AccessToken 的聊天对话解析器
 */
@Slf4j
@Component
public class AccessTokenChatResponseParser implements ResponseParser<ChatWebConversationResponse> {

    @Override
    public ChatWebConversationResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, ChatWebConversationResponse.class);
    }

    @Override
    public String parseReceivedMessage(String receivedMessage, String newMessage) {
        return newMessage;
    }

    @Override
    public String parseNewMessage(String originalData) {
        // 不为 JSON 直接返回 null，不知道什么情况触发，但是不属于正文
        if (!JSONUtil.isTypeJSON(originalData)) {
            return null;
        }
        ChatWebConversationResponse.Message message = parseSuccess(originalData).getMessage();
        if (Objects.isNull(message)) {
            return null;
        }
        ChatWebConversationResponse.Author author = message.getAuthor();
        if (!author.getRole().equals(Message.Role.ASSISTANT.getName())) {
            return null;
        }

        // 只需要 role=assistant 的消息
        List<String> parts = message.getContent().getParts();
        if (CollectionUtil.isEmpty(parts)) {
            return null;
        }

        // AccessToken 模式返回的消息每句都会包含前面的话，不需要手动拼接
        return parts.get(0);
    }

    @Override
    public String parseErrorMessage(String originalResponseErrorMsg) {
        try {
            // 首先得满足 json 格式，有些情况比如网页直接 502 非接口返回的就是 html，也有可能是网络故障
            if (!JSONUtil.isTypeJSON(originalResponseErrorMsg)) {
                return "网络故障，请稍后再试";
            }

            // 提取 code
            JsonNode detailJsonNode = ObjectMapperUtil.readTree(originalResponseErrorMsg).get("detail");
            JsonNode codeJsonNode = detailJsonNode.get("code");
            // 如果没有 code 节点，则直接取 detail 的文字
            if (Objects.isNull(codeJsonNode)) {
                return AccessTokenChatErrorCodeEnum.DETAIL_MAP.get(detailJsonNode.asText()).getMessage();
            }
            String code = codeJsonNode.asText();

            // 通过 code 获取
            AccessTokenChatErrorCodeEnum errorCodeEnum = Optional.ofNullable(code).map(AccessTokenChatErrorCodeEnum.CODE_MAP::get).orElse(null);

            if (Objects.isNull(errorCodeEnum)) {
                return "未知编码错误，请稍后再试";
            }
            return errorCodeEnum.getMessage();
        } catch (Exception e) {
            log.error("AccessToken 解析错误信息失败，错误信息：{}", originalResponseErrorMsg, e);
            return "未知解析错误，请稍后再试";
        }
    }

    @Override
    public RoomOpenAiChatMsgVO parseChatMessageVO(RoomOpenAiChatMessageStorage chatMessageStorage) {
        if (Objects.isNull(chatMessageStorage.getAnswerMessageDO())) {
            return null;
        }
        RoomOpenAiChatMsgVO roomOpenAiChatMsgVO = new RoomOpenAiChatMsgVO();
        RoomOpenAiChatWebMsgDO answerMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getAnswerMessageDO();
        roomOpenAiChatMsgVO.setId(answerMessage.getId());
        roomOpenAiChatMsgVO.setContent(chatMessageStorage.getReceivedMessage());
        roomOpenAiChatMsgVO.setMessageType(MessageTypeEnum.ANSWER);
        roomOpenAiChatMsgVO.setCreateTime(answerMessage.getCreateTime());
        return roomOpenAiChatMsgVO;
    }
}
