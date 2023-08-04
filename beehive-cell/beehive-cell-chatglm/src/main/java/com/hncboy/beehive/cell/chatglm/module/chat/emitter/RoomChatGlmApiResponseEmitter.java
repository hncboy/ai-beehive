package com.hncboy.beehive.cell.chatglm.module.chat.emitter;

import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomChatGlmMsgStatusEnum;
import com.hncboy.beehive.base.util.*;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import com.hncboy.beehive.cell.chatglm.module.chat.api.*;
import com.hncboy.beehive.cell.chatglm.module.chat.parser.ChatGlmCompletionResponseParser;
import com.hncboy.beehive.cell.chatglm.service.RoomChatGlmMsgService;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.chatglm.domain.request.RoomChatGlmSendRequest;
import com.hncboy.beehive.cell.chatglm.module.chat.listener.ParsedEventSourceListener;
import com.hncboy.beehive.cell.chatglm.module.chat.listener.ChatGlmResponseBodyEmitterStreamListener;
import com.hncboy.beehive.cell.chatglm.module.chat.storage.ChatGlmDatabaseDataStorage;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.*;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话房间消息响应处理
 */
@Lazy
@Component
public class RoomChatGlmApiResponseEmitter implements RoomChatGlmResponseEmitter {

    @Resource
    private ChatGlmCompletionResponseParser parser;

    @Resource
    private ChatGlmDatabaseDataStorage dataStorage;

    @Resource
    private RoomChatGlmMsgService roomChatGLMMsgService;

    @Override
    public void requestToResponseEmitter(RoomChatGlmSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy) {
        // 获取房间配置参数
        Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());

        // 初始化问题消息
        RoomChatGlmMsgDO questionMessage = initQuestionMessage(sendRequest, roomConfigParamAsMap);

        // 构建上下文消息
        LinkedList<Message> contentMessages = ChatGlmCompletionBuildUtil.buildContextMessage(questionMessage, roomConfigParamAsMap);
        // 获取上下文的 token 数量设置 promptTokens
        questionMessage.setPromptTokens(TikTokensUtil.tokens(ChatCompletion.Model.GPT_3_5_TURBO.getName(), contentMessages));

        // 构建聊天对话请求参数
        ChatCompletion chatCompletion = ChatGlmCompletionBuildUtil.buildChatCompletion(questionMessage, roomConfigParamAsMap, contentMessages);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(chatCompletion));

        // 构建错误内容处理节点
        List<ChatGlmErrorNode> chatErrorNodes = new ArrayList<>();
        chatErrorNodes.add(SpringUtil.getBean(ChatGlmTokenLimitErrorNode.class));
        chatErrorNodes.add(SpringUtil.getBean(ChatGlmLocalSensitiveWordErrorNode.class));
        for (ChatGlmErrorNode chatErrorNode : chatErrorNodes) {
            Pair<Boolean, String> errorHandleResultPair = chatErrorNode.doHandle(questionMessage, roomConfigParamAsMap);
            if (!errorHandleResultPair.getKey()) {
                // 保存问题消息
                roomChatGLMMsgService.save(questionMessage);
                // 保存错误的回答消息
                saveErrorAnswerQuestion(questionMessage, errorHandleResultPair.getValue());
                // 发送错误消息
                ResponseBodyEmitterUtil.sendWithComplete(emitter, errorHandleResultPair.getValue());
                return;
            }
        }

        // 保存问题消息
        roomChatGLMMsgService.save(questionMessage);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .setListener(new ChatGlmResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setQuestionMessageDO(questionMessage)
                .build();

        // 构建 OpenAiStreamClient
        OpenAiStreamClient openAiStreamClient = OpenAiStreamClient.builder()
                .okHttpClient(OkHttpClientUtil.getProxyInstance())
                .apiKey(Collections.singletonList("xxx"))
                .apiHost(roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.CHATGLM_BASE_URL).asString())
                .build();
        openAiStreamClient.streamChatCompletion(chatCompletion, parsedEventSourceListener);
    }

    /**
     * 初始化问题消息
     *
     * @param sendRequest          发送的消息
     * @param roomConfigParamAsMap 房间配置参数
     * @return 问题消息
     */
    private RoomChatGlmMsgDO initQuestionMessage(RoomChatGlmSendRequest sendRequest, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        RoomChatGlmMsgDO questionMessage = new RoomChatGlmMsgDO();
        questionMessage.setId(IdWorker.getId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setRoomId(sendRequest.getRoomId());
        questionMessage.setIp(WebUtil.getIp());
        questionMessage.setMessageType(MessageTypeEnum.QUESTION);
        questionMessage.setModelName(roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.MODEL).asString());
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));
        questionMessage.setContent(sendRequest.getContent());
        questionMessage.setStatus(RoomChatGlmMsgStatusEnum.INIT);
        return questionMessage;
    }

    /**
     * 保存错误消息
     *
     * @param questionMessage 问题消息
     * @param content         错误内容
     */
    private void saveErrorAnswerQuestion(RoomChatGlmMsgDO questionMessage, String content) {
        RoomChatGlmMsgDO answerMessage = new RoomChatGlmMsgDO();
        answerMessage.setId(IdWorker.getId());
        answerMessage.setUserId(questionMessage.getUserId());
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setParentQuestionMessageId(questionMessage.getId());
        answerMessage.setMessageType(MessageTypeEnum.ANSWER);
        answerMessage.setModelName(questionMessage.getModelName());
        answerMessage.setIp(questionMessage.getIp());
        answerMessage.setApiKey(questionMessage.getApiKey());
        answerMessage.setContent(content);
        answerMessage.setOriginalData(null);
        answerMessage.setPromptTokens(0);
        answerMessage.setCompletionTokens(0);
        answerMessage.setTotalTokens(0);
        answerMessage.setStatus(questionMessage.getStatus());
        roomChatGLMMsgService.save(answerMessage);
    }
}
