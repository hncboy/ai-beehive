package cn.beehive.cell.openai.module.chat.parser;

import cn.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import cn.beehive.cell.openai.module.chat.accesstoken.ChatWebConversationResponse;
import cn.beehive.cell.openai.module.chat.storage.RoomOpenAiChatMessageStorage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * AccessToken 的聊天对话解析器
 */
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
