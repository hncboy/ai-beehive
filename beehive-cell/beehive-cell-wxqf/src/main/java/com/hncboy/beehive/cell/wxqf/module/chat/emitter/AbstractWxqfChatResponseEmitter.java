package com.hncboy.beehive.cell.wxqf.module.chat.emitter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.ContentType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.base.enums.RoomWxqfChatMsgStatusEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonRequest;
import com.hncboy.beehive.cell.wxqf.service.RoomWxqfChatMsgService;
import jakarta.annotation.Resource;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息响应转 Emitter
 */
public abstract class AbstractWxqfChatResponseEmitter {

    @Resource
    protected RoomWxqfChatMsgService roomWxqfChatMsgService;

    /**
     * 消息请求转 Emitter
     *
     * @param sendRequest        消息处理请求
     * @param emitter            ResponseBodyEmitter
     * @param cellConfigStrategy cell 配置项策略
     */
    public abstract void requestToResponseEmitter(RoomWxqfChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy);

    /**
     * 构建对话 API 通用请求
     *
     * @param roomWxqfChatMsgDO 问题消息
     * @param contextCount      上下文条数
     * @param relatedTimeHour   上下文关联时间
     * @param userId            用户 ID
     */
    WxqfChatApiCommonRequest buildChatApiCommonRequest(RoomWxqfChatMsgDO roomWxqfChatMsgDO, int contextCount, int relatedTimeHour, String userId) {
        WxqfChatApiCommonRequest wxqfChatApiCommonRequest = new WxqfChatApiCommonRequest();
        wxqfChatApiCommonRequest.setMessages(buildContextMessage(roomWxqfChatMsgDO, contextCount, relatedTimeHour));
        wxqfChatApiCommonRequest.setStream(true);
        wxqfChatApiCommonRequest.setUserId(userId);
        return wxqfChatApiCommonRequest;
    }

    /**
     * 问答接口 stream 形式
     *
     * @param chatApiCommonRequest 对话请求参数
     * @param accessToken          AccessToken
     * @param requestUrl           请求 URL
     * @param eventSourceListener  事件监听器
     */
    void streamChatCompletions(WxqfChatApiCommonRequest chatApiCommonRequest, String accessToken, String requestUrl, EventSourceListener eventSourceListener) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        httpBuilder.addQueryParameter("access_token", accessToken);

        // 构建 Request
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(RequestBody.create(ObjectMapperUtil.toJson(chatApiCommonRequest), MediaType.parse(ContentType.JSON.getValue())))
                .build();
        // 创建事件
        OkHttpClient okHttpClient = OkHttpClientUtil.getInstance();
        EventSources.createFactory(okHttpClient).newEventSource(request, eventSourceListener);
    }

    /**
     * 初始化问题消息
     *
     * @param sendRequest          发送的消息
     * @param roomConfigParamJson 房间配置参数
     * @return 问题消息
     */
    RoomWxqfChatMsgDO initQuestionMessage(RoomWxqfChatSendRequest sendRequest) {
        RoomWxqfChatMsgDO questionMessage = new RoomWxqfChatMsgDO();
        questionMessage.setId(IdWorker.getId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setRoomId(sendRequest.getRoomId());
        questionMessage.setIp(WebUtil.getIp());
        questionMessage.setMessageType(MessageTypeEnum.QUESTION);
        questionMessage.setContent(sendRequest.getContent());
        questionMessage.setStatus(RoomWxqfChatMsgStatusEnum.INIT);
        return questionMessage;
    }

    /**
     * 构建上下文消息
     *
     * @param questionMessage 问题消息
     * @param contextCount    上下文条数
     * @param relatedTimeHour 上下文关联时间
     */
    private LinkedList<WxqfChatApiCommonRequest.Message> buildContextMessage(RoomWxqfChatMsgDO questionMessage, int contextCount, int relatedTimeHour) {
        LinkedList<WxqfChatApiCommonRequest.Message> contextMessages = new LinkedList<>();

        // 上下文条数
        // 不关联上下文，构建当前消息就直接返回
        if (contextCount == 0) {
            contextMessages.add(WxqfChatApiCommonRequest.Message.builder()
                    .role(WxqfChatApiCommonRequest.Role.USER)
                    .content(questionMessage.getContent())
                    .build());
            return contextMessages;
        }

        // 查询上下文消息
        List<RoomWxqfChatMsgDO> historyMessages = roomWxqfChatMsgService.list(new LambdaQueryWrapper<RoomWxqfChatMsgDO>()
                // 查询需要的字段
                .select(RoomWxqfChatMsgDO::getMessageType, RoomWxqfChatMsgDO::getContent)
                // 当前房间
                .eq(RoomWxqfChatMsgDO::getRoomId, questionMessage.getRoomId())
                // 查询消息为成功的
                .eq(RoomWxqfChatMsgDO::getStatus, RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS)
                // 上下文的时间范围
                .gt(relatedTimeHour > 0, RoomWxqfChatMsgDO::getCreateTime, DateUtil.offsetHour(new Date(), -relatedTimeHour))
                // 限制上下文条数
                .last("limit " + contextCount)
                // 按主键降序
                .orderByDesc(RoomWxqfChatMsgDO::getId));

        // 这里降序用来取出最新的上下文消息，然后再反转
        Collections.reverse(historyMessages);
        for (RoomWxqfChatMsgDO historyMessage : historyMessages) {
            WxqfChatApiCommonRequest.Role role = (historyMessage.getMessageType() == MessageTypeEnum.ANSWER) ? WxqfChatApiCommonRequest.Role.ASSISTANT : WxqfChatApiCommonRequest.Role.USER;
            contextMessages.add(WxqfChatApiCommonRequest.Message.builder()
                    .role(role)
                    .content(historyMessage.getContent())
                    .build());
        }

        // 历史消息必须是偶数
        if (contextMessages.size() % 2 != 0) {
            // 移除第一条
            contextMessages.removeFirst();
        }

        // 查询当前用户消息
        contextMessages.add(WxqfChatApiCommonRequest.Message.builder()
                .role(WxqfChatApiCommonRequest.Role.USER)
                .content(questionMessage.getContent())
                .build());

        return contextMessages;
    }
}
