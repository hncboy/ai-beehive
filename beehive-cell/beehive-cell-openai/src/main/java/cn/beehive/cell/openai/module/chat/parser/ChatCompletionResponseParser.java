package cn.beehive.cell.openai.module.chat.parser;

import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import cn.beehive.cell.openai.module.chat.storage.RoomOpenAiChatMessageStorage;
import cn.hutool.core.collection.CollectionUtil;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * ApiKey 的 ChatCompletion 响应解析器
 */
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
        RoomOpenAiChatMsgDO roomOpenAiChatMsgDO = (RoomOpenAiChatMsgDO) chatMessageStorage.getAnswerMessageDO();
        roomOpenAiChatMsgVO.setId(roomOpenAiChatMsgDO.getId());
        roomOpenAiChatMsgVO.setContent(chatMessageStorage.getReceivedMessage());
        roomOpenAiChatMsgVO.setMessageType(MessageTypeEnum.ANSWER);
        roomOpenAiChatMsgVO.setCreateTime(roomOpenAiChatMsgDO.getCreateTime());
        return roomOpenAiChatMsgVO;
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
