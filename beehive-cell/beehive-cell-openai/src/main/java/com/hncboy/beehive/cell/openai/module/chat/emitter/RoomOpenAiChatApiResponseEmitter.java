package com.hncboy.beehive.cell.openai.module.chat.emitter;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatApiModelEnum;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import com.hncboy.beehive.cell.openai.module.chat.apikey.ChatBaiduAipErrorNode;
import com.hncboy.beehive.cell.openai.module.chat.apikey.ChatCompletionBuildUtil;
import com.hncboy.beehive.cell.openai.module.chat.apikey.ChatErrorNode;
import com.hncboy.beehive.cell.openai.module.chat.apikey.ChatLocalSensitiveWordErrorNode;
import com.hncboy.beehive.cell.openai.module.chat.apikey.ChatTokenLimitErrorNode;
import com.hncboy.beehive.cell.openai.module.chat.listener.ConsoleStreamListener;
import com.hncboy.beehive.cell.openai.module.chat.listener.ParsedEventSourceListener;
import com.hncboy.beehive.cell.openai.module.chat.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.beehive.cell.openai.module.chat.parser.ChatCompletionResponseParser;
import com.hncboy.beehive.cell.openai.module.chat.storage.ApiKeyDatabaseDataStorage;
import com.hncboy.beehive.cell.openai.module.key.OpenAiApiKeyHandler;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023-3-24
 * OpenAi 对话房间消息响应处理
 */
@Lazy
@Component
public class RoomOpenAiChatApiResponseEmitter implements RoomOpenAiChatResponseEmitter {

    @Resource
    private ChatCompletionResponseParser parser;

    @Resource
    private ApiKeyDatabaseDataStorage dataStorage;

    @Resource
    private RoomOpenAiChatMsgService roomOpenAiChatMsgService;

    @Override
    public void requestToResponseEmitter(RoomOpenAiChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy) {
        // 获取房间配置参数
        Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());

        // 获取 ApiKey 相关信息
        Pair<String, String> chatApiKeyInfoPair = OpenAiApiKeyHandler.getChatApiKeyInfo(
                roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.API_KEY).asString(),
                roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.OPENAI_BASE_URL).asString(),
                roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.KEY_STRATEGY).asString(),
                roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.MODEL).asString());
        // 覆盖原始值
        roomConfigParamAsMap.put(OpenAiChatCellConfigCodeEnum.API_KEY, new DataWrapper(chatApiKeyInfoPair.getKey()));
        roomConfigParamAsMap.put(OpenAiChatCellConfigCodeEnum.OPENAI_BASE_URL, new DataWrapper(chatApiKeyInfoPair.getValue()));

        // 初始化问题消息
        RoomOpenAiChatMsgDO questionMessage = initQuestionMessage(sendRequest, roomConfigParamAsMap);

        // 构建上下文消息
        LinkedList<Message> contentMessages = ChatCompletionBuildUtil.buildContextMessage(questionMessage, roomConfigParamAsMap);
        // 获取上下文的 token 数量设置 promptTokens
        questionMessage.setPromptTokens(TikTokensUtil.tokens(OpenAiChatApiModelEnum.NAME_MAP.get(questionMessage.getModelName()).getCalcTokenModelName(), contentMessages));

        // 构建聊天对话请求参数
        ChatCompletion chatCompletion = ChatCompletionBuildUtil.buildChatCompletion(questionMessage, roomConfigParamAsMap, contentMessages);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(chatCompletion));

        // 构建错误内容处理节点
        List<ChatErrorNode> chatErrorNodes = new ArrayList<>();
        chatErrorNodes.add(SpringUtil.getBean(ChatTokenLimitErrorNode.class));
        chatErrorNodes.add(SpringUtil.getBean(ChatLocalSensitiveWordErrorNode.class));
        chatErrorNodes.add(SpringUtil.getBean(ChatBaiduAipErrorNode.class));
        for (ChatErrorNode chatErrorNode : chatErrorNodes) {
            Pair<Boolean, String> errorHandleResultPair = chatErrorNode.doHandle(questionMessage, roomConfigParamAsMap);
            if (!errorHandleResultPair.getKey()) {
                // 保存问题消息
                roomOpenAiChatMsgService.save(questionMessage);
                // 保存错误的回答消息
                saveErrorAnswerQuestion(questionMessage, errorHandleResultPair.getValue());
                // 发送错误消息
                ResponseBodyEmitterUtil.sendWithComplete(emitter, errorHandleResultPair.getValue());
                return;
            }
        }

        // 保存问题消息
        roomOpenAiChatMsgService.save(questionMessage);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setQuestionMessageDO(questionMessage)
                .build();

        // 构建 OpenAiStreamClient
        OpenAiStreamClient openAiStreamClient = OpenAiStreamClient.builder()
                .okHttpClient(OkHttpClientUtil.getProxyInstance())
                .apiKey(Collections.singletonList(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.API_KEY).asString()))
                .apiHost(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.OPENAI_BASE_URL).asString())
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
    private RoomOpenAiChatMsgDO initQuestionMessage(RoomOpenAiChatSendRequest sendRequest, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        RoomOpenAiChatMsgDO questionMessage = new RoomOpenAiChatMsgDO();
        questionMessage.setId(IdWorker.getId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setRoomId(sendRequest.getRoomId());
        questionMessage.setIp(WebUtil.getIp());
        questionMessage.setMessageType(MessageTypeEnum.QUESTION);
        questionMessage.setModelName(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.MODEL).asString());
        questionMessage.setApiKey(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.API_KEY).asString());
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));
        questionMessage.setContent(sendRequest.getContent());
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.INIT);
        return questionMessage;
    }

    /**
     * 保存错误消息
     *
     * @param questionMessage 问题消息
     * @param content         错误内容
     */
    private void saveErrorAnswerQuestion(RoomOpenAiChatMsgDO questionMessage, String content) {
        RoomOpenAiChatMsgDO answerMessage = new RoomOpenAiChatMsgDO();
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
        roomOpenAiChatMsgService.save(answerMessage);
    }
}
