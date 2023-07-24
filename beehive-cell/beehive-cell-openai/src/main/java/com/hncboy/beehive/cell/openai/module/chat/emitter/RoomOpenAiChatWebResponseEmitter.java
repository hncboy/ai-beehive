package com.hncboy.beehive.cell.openai.module.chat.emitter;

import com.hncboy.beehive.base.constant.AuthConstant;
import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatWebCellConfigCodeEnum;
import com.hncboy.beehive.cell.openai.module.chat.accesstoken.ChatWebConversationRequest;
import com.hncboy.beehive.cell.openai.module.chat.listener.ConsoleStreamListener;
import com.hncboy.beehive.cell.openai.module.chat.listener.ParsedEventSourceListener;
import com.hncboy.beehive.cell.openai.module.chat.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.beehive.cell.openai.module.chat.parser.AccessTokenChatResponseParser;
import com.hncboy.beehive.cell.openai.module.chat.storage.AccessTokenDatabaseDataStorage;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatWebMsgService;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.unfbx.chatgpt.entity.chat.Message;
import jakarta.annotation.Resource;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Collections;
import java.util.Map;


/**
 * @author hncboy
 * @date 2023-3-24
 * AccessToken 响应处理
 */
@Component
public class RoomOpenAiChatWebResponseEmitter implements RoomOpenAiChatResponseEmitter {

    @Resource
    private AccessTokenChatResponseParser parser;

    @Resource
    private AccessTokenDatabaseDataStorage dataStorage;

    @Resource
    private RoomOpenAiChatWebMsgService roomOpenAiChatWebMsgService;

    @Override
    public void requestToResponseEmitter(RoomOpenAiChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy) {
        // 获取房间配置参数
        Map<OpenAiChatWebCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());
        // 初始化问题消息
        RoomOpenAiChatWebMsgDO questionMessage = roomOpenAiChatWebMsgService.initQuestionMessage(sendRequest, roomConfigParamAsMap);
        // 构建 ConversationRequest
        ChatWebConversationRequest conversationRequest = buildConversationRequest(questionMessage);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(conversationRequest));
        // 保存问题消息
        roomOpenAiChatWebMsgService.save(questionMessage);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setQuestionMessageDO(questionMessage)
                .build();

        // 发送请求
        streamChatCompletions(conversationRequest, roomConfigParamAsMap, parsedEventSourceListener);
    }

    /**
     * 问答接口 stream 形式
     *
     * @param conversationRequest  对话请求参数
     * @param roomConfigParamAsMap 房间配置参数
     * @param eventSourceListener  事件监听器
     */
    public void streamChatCompletions(ChatWebConversationRequest conversationRequest, Map<OpenAiChatWebCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap, EventSourceListener eventSourceListener) {
        // 获取 AccessToken
        String accessToken = roomConfigParamAsMap.get(OpenAiChatWebCellConfigCodeEnum.ACCESS_TOKEN).asString();
        if (!accessToken.startsWith(AuthConstant.BEARER)) {
            accessToken = AuthConstant.BEARER.concat(accessToken);
        }

        // 构建请求头
        Headers headers = new Headers.Builder()
                .add(Header.AUTHORIZATION.name(), accessToken)
                .add(Header.ACCEPT.name(), "text/event-stream")
                .add(Header.CONTENT_TYPE.name(), ContentType.JSON.getValue())
                .build();

        // 构建 Request
        Request request = new Request.Builder()
                .url(roomConfigParamAsMap.get(OpenAiChatWebCellConfigCodeEnum.PROXY_URL).asString())
                .post(RequestBody.create(ObjectMapperUtil.toJson(conversationRequest), MediaType.parse(ContentType.JSON.getValue())))
                .headers(headers)
                .build();
        // 创建事件
        OkHttpClient okHttpClient = OkHttpClientUtil.getInstance();
        EventSources.createFactory(okHttpClient).newEventSource(request, eventSourceListener);
    }

    /**
     * 构建 ConversationRequest
     *
     * @param questionMessage 聊天消息
     * @return ConversationRequest
     */
    private ChatWebConversationRequest buildConversationRequest(RoomOpenAiChatWebMsgDO questionMessage) {
        // 构建 content
        ChatWebConversationRequest.Content content = ChatWebConversationRequest.Content.builder()
                .parts(Collections.singletonList(questionMessage.getContent()))
                .build();

        // 构建 Message
        ChatWebConversationRequest.Message message = ChatWebConversationRequest.Message.builder()
                .id(questionMessage.getRequestMessageId())
                .author(ChatWebConversationRequest.Author.builder().role(Message.Role.USER.getName()).build())
                .content(content)
                .build();

        return ChatWebConversationRequest.builder()
                .messages(Collections.singletonList(message))
                .action(ChatWebConversationRequest.MessageActionTypeEnum.NEXT)
                .model(questionMessage.getModelName())
                // 父级消息 id 不能为空，不然会报错，因此第一条消息也需要随机生成一个
                .parentMessageId(questionMessage.getRequestParentMessageId())
                // 不是第一条消息，需要传对话 id，否则传空
                .conversationId(questionMessage.getRequestConversationId())
                .build();
    }
}
