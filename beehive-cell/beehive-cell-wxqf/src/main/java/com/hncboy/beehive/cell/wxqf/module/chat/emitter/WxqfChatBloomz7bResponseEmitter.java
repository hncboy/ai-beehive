package com.hncboy.beehive.cell.wxqf.module.chat.emitter;

import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.enums.WxqfChatBloomz7bCellConfigCodeEnum;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonRequest;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ConsoleStreamListener;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ParsedEventSourceListener;
import com.hncboy.beehive.cell.wxqf.module.chat.listener.ResponseBodyEmitterStreamListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/7/25
 * 文心千帆 BLOOMZ-7B 对话房间消息响应处理
 */
@Lazy
@Component
public class WxqfChatBloomz7bResponseEmitter extends AbstractWxqfChatResponseEmitter {

    @Override
    public void requestToResponseEmitter(RoomWxqfChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy) {
        // 获取房间配置参数
        Map<WxqfChatBloomz7bCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());
        CellCodeEnum cellCodeEnum = cellConfigStrategy.getCellCode();

        // 初始化问题消息
        RoomWxqfChatMsgDO questionMessage = initQuestionMessage(sendRequest);
        questionMessage.setModelName(cellCodeEnum.getCode());
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));

        // 构建请求参数
        // 上下文条数
        int contextCount = roomConfigParamAsMap.get(WxqfChatBloomz7bCellConfigCodeEnum.CONTEXT_COUNT).asInt();
        // 上下文关联时间
        int relatedTimeHour = roomConfigParamAsMap.get(WxqfChatBloomz7bCellConfigCodeEnum.CONTEXT_RELATED_TIME_HOUR).asInt();
        String userId = roomConfigParamAsMap.get(WxqfChatBloomz7bCellConfigCodeEnum.USER_ID).asString();
        WxqfChatApiCommonRequest wxqfChatApiCommonRequest = buildChatApiCommonRequest(questionMessage, contextCount, relatedTimeHour, userId);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(wxqfChatApiCommonRequest));

        // 保存问题消息
        roomWxqfChatMsgService.save(questionMessage);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setQuestionMessageDO(questionMessage)
                .build();

        // 发送请求
        String accessToken = roomConfigParamAsMap.get(WxqfChatBloomz7bCellConfigCodeEnum.ACCESS_TOKEN).asString();
        String requestUrl = roomConfigParamAsMap.get(WxqfChatBloomz7bCellConfigCodeEnum.REQUEST_URL).asString();
        streamChatCompletions(wxqfChatApiCommonRequest, accessToken, requestUrl, parsedEventSourceListener);
    }
}
