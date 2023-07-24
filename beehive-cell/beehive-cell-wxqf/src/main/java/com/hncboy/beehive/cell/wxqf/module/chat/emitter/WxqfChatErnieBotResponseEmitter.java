package com.hncboy.beehive.cell.wxqf.module.chat.emitter;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomWxqfChatMsgStatusEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.enums.WxqfChatErnieBotCellConfigCodeEnum;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonRequest;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatErnieBotApiRequest;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ConsoleStreamListener;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ParsedEventSourceListener;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ResponseBodyEmitterStreamListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆 ERNIE-Bot 对话房间消息响应处理
 */
@Lazy
@Component
public class WxqfChatErnieBotResponseEmitter extends AbstractWxqfChatResponseEmitter {

    @Override
    public void requestToResponseEmitter(RoomWxqfChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy) {
        // 获取房间配置参数
        Map<WxqfChatErnieBotCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());
        CellCodeEnum cellCodeEnum = cellConfigStrategy.getCellCode();

        // 初始化问题消息
        RoomWxqfChatMsgDO questionMessage = initQuestionMessage(sendRequest);
        questionMessage.setModelName(cellCodeEnum.getCode());
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));

        // 构建请求参数
        WxqfChatErnieBotApiRequest wxqfChatErnieBotApiRequest = buildChatErnieBotApiRequest(questionMessage, roomConfigParamAsMap);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(wxqfChatErnieBotApiRequest));

        // 保存问题消息
        roomWxqfChatMsgService.save(questionMessage);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setQuestionMessageDO(questionMessage)
                .build();

        // 发送请求
        String accessToken = roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.ACCESS_TOKEN).asString();
        String requestUrl = roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.REQUEST_URL).asString();
        streamChatCompletions(wxqfChatErnieBotApiRequest, accessToken, requestUrl, parsedEventSourceListener);
    }

    /**
     * 构建 ConversationRequest
     *
     * @param questionMessage 聊天消息
     * @return ConversationRequest
     */
    private WxqfChatErnieBotApiRequest buildChatErnieBotApiRequest(RoomWxqfChatMsgDO questionMessage, Map<WxqfChatErnieBotCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        // 上下文条数
        int contextCount = roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.CONTEXT_COUNT).asInt();
        // 上下文关联时间
        int relatedTimeHour = roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.CONTEXT_RELATED_TIME_HOUR).asInt();
        String userId = roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.USER_ID).asString();
        WxqfChatApiCommonRequest wxqfChatApiCommonRequest = buildChatApiCommonRequest(questionMessage, contextCount, relatedTimeHour, userId);

        WxqfChatErnieBotApiRequest wxqfChatErnieBotApiRequest = new WxqfChatErnieBotApiRequest();
        wxqfChatErnieBotApiRequest.setTemperature(roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.TEMPERATURE).asBigDecimal());
        wxqfChatErnieBotApiRequest.setTopP(roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.TOP_P).asBigDecimal());
        wxqfChatErnieBotApiRequest.setPenaltyScore(roomConfigParamAsMap.get(WxqfChatErnieBotCellConfigCodeEnum.PENALTY_SCORE).asBigDecimal());
        wxqfChatErnieBotApiRequest.setMessages(wxqfChatApiCommonRequest.getMessages());
        wxqfChatErnieBotApiRequest.setStream(true);
        wxqfChatErnieBotApiRequest.setUserId(wxqfChatApiCommonRequest.getUserId());

        return wxqfChatErnieBotApiRequest;
    }
}
