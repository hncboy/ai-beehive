package com.hncboy.beehive.cell.bing.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import com.hncboy.beehive.base.domain.entity.RoomBingMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.cell.bing.constant.NewBingConstant;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiSendMessageResultBO;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiSendThrottlingResultBO;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiSendType1ResultBO;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiSendType2ResultBO;
import com.hncboy.beehive.cell.bing.domain.bo.BingRoomBO;
import com.hncboy.beehive.cell.bing.domain.vo.RoomBingStreamMsgVO;
import com.hncboy.beehive.cell.bing.handler.converter.RoomBingMsgConverter;
import com.hncboy.beehive.cell.bing.service.RoomBingMsgService;
import com.hncboy.beehive.cell.bing.service.RoomBingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/5/28
 * Bing Api type 响应消息处理器
 */
@Slf4j
@Component
public class BingApiResponseTypeMessageHandler {

    @Resource
    private RoomBingService roomBingService;

    @Resource
    private RoomBingMsgService roomBingMsgService;

    /**
     * 处理 type=1 消息
     * 用来接收流式的响应消息
     *
     * @param receiveMessage 消息
     * @param emitter        响应流
     */
    public void handleType1(String receiveMessage, ResponseBodyEmitter emitter) {
        BingApiSendType1ResultBO resultBO = ObjectMapperUtil.fromJson(receiveMessage, BingApiSendType1ResultBO.class);
        BingApiSendType1ResultBO.Argument argument = resultBO.getArguments().get(0);

        if (Objects.nonNull(argument.getMessages())) {
            String responseText = argument.getMessages().get(0).getText();
            RoomBingStreamMsgVO roomBingStreamMsgVO = new RoomBingStreamMsgVO();
            roomBingStreamMsgVO.setContent(responseText);
            // 发送消息
            ResponseBodyEmitterUtil.send(emitter, roomBingStreamMsgVO);
        }
    }

    /**
     * 处理 type=2 消息
     * 响应结果，包含正常和异常结束
     *
     * @param questionMessage 问题消息
     * @param bingRoomBO      房间业务信息
     * @param receiveMessage  消息
     * @param emitter         响应流
     * @return 是否需要开启新话题
     */
    public boolean handleType2(RoomBingMsgDO questionMessage, BingRoomBO bingRoomBO, String receiveMessage, ResponseBodyEmitter emitter) {
        BingApiSendType2ResultBO resultBO = ObjectMapperUtil.fromJson(receiveMessage, BingApiSendType2ResultBO.class);
        String resultValue = resultBO.getItem().getResult().getValue();

        // 成功情况
        if (Objects.equals(resultValue, NewBingConstant.RESPONSE_SUCCESS)) {
            List<BingApiSendMessageResultBO> messages = resultBO.getItem().getMessages();
            // 过滤出机器人回复的那一条
            List<BingApiSendMessageResultBO> botMessages = messages.stream().filter(message -> Objects.equals(message.getAuthor(), "bot")).toList();
            if (CollectionUtil.isEmpty(botMessages)) {
                bingRoomBO.setRefreshRoomReason("bing 生气了，不想回复了，需要开启新话题");
                roomBingService.refreshRoom(bingRoomBO);
                return true;
            }

            // 获取回复消息
            BingApiSendMessageResultBO botMessage = botMessages.get(0);
            RoomBingStreamMsgVO roomBingStreamMsgVO = new RoomBingStreamMsgVO();
            roomBingStreamMsgVO.setContent(botMessage.getText());

            // 提取建议
            List<BingApiSendMessageResultBO.SuggestedResponse> suggestedResponses = botMessage.getSuggestedResponses();
            if (CollectionUtil.isNotEmpty(suggestedResponses)) {
                roomBingStreamMsgVO.setSuggests(suggestedResponses.stream().map(BingApiSendMessageResultBO.SuggestedResponse::getText).toList());
            }

            // 提取用户提问次数，可能会发生变化，不清楚什么情况会变
            BingApiSendThrottlingResultBO throttling = resultBO.getItem().getThrottling();
            roomBingStreamMsgVO.setNumUserMessagesInConversation(throttling.getNumUserMessagesInConversation());
            roomBingStreamMsgVO.setMaxNumUserMessagesInConversation(throttling.getMaxNumUserMessagesInConversation());
            // 超过最大提问次数会一直回复：Thanks for this conversation! I've reached my limit, will you hit “New topic,” please?
            if (throttling.getNumUserMessagesInConversation() > throttling.getMaxNumUserMessagesInConversation()) {
                bingRoomBO.setRefreshRoomReason("超过最大提问次数，需要开启新话题");
                roomBingService.refreshRoom(bingRoomBO);
                return true;
            }

            // 更新房间提问次数
            RoomBingDO roomBingDO = bingRoomBO.getRoomBingDO();
            roomBingDO.setMaxNumUserMessagesInConversation(throttling.getMaxNumUserMessagesInConversation());
            roomBingDO.setNumUserMessagesInConversation(throttling.getNumUserMessagesInConversation());
            roomBingService.updateById(roomBingDO);

            // 增加回答消息
            RoomBingMsgDO answerMessage = RoomBingMsgConverter.INSTANCE.bingRoomBoToEntity(bingRoomBO);
            answerMessage.setParentMessageId(questionMessage.getId());
            answerMessage.setIp(questionMessage.getIp());
            answerMessage.setType(MessageTypeEnum.ANSWER);
            answerMessage.setContent(botMessage.getText());
            answerMessage.setSuggestResponses(Optional.ofNullable(roomBingStreamMsgVO.getSuggests()).orElse(Collections.emptyList()));
            answerMessage.setSourceAttributions(ObjectMapperUtil.fromJson(ObjectMapperUtil.toJson(botMessage.getSourceAttributions()), new TypeReference<>() {
            }));
            roomBingMsgService.save(answerMessage);

            // 发送消息
            ResponseBodyEmitterUtil.send(emitter, roomBingStreamMsgVO);

            // 成功
            return false;
        }

        // 非成功就开启新话题
        log.debug("NewBing 非成功状态，状态为 {}，需要开启新话题", resultValue);
        bingRoomBO.setRefreshRoomReason("非成功状态，状态为 " + resultValue);
        roomBingService.refreshRoom(bingRoomBO);
        return true;
    }
}
