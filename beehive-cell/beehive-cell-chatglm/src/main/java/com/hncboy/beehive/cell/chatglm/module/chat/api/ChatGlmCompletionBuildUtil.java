package com.hncboy.beehive.cell.chatglm.module.chat.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomChatGlmMsgStatusEnum;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmApiModelEnum;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import com.hncboy.beehive.cell.chatglm.service.RoomChatGlmMsgService;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;

import java.util.*;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话消息构建工具
 */
public class ChatGlmCompletionBuildUtil {

    /**
     * 构建聊天对话请求参数
     *
     * @param questionMessage      问题消息
     * @param roomConfigParamAsMap 房间配置参数
     * @param contentMessages      上下文消息
     * @return 聊天对话请求参数
     */
    public static ChatCompletion buildChatCompletion(RoomChatGlmMsgDO questionMessage, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap, LinkedList<Message> contentMessages) {
        // 最终的 maxTokens
        int finalMaxTokens;

        //开源版本默认为CHATGLM2_6B
        if (StrUtil.isBlank(questionMessage.getModelName())) {
            questionMessage.setModelName(ChatGlmApiModelEnum.CHATGLM2_6B.getName());
        }

        // 本次对话剩余的 maxTokens 最大值 = 模型的最大上限 - 本次 prompt 消耗的 tokens - 1
        int currentRemainMaxTokens = ChatGlmApiModelEnum.maxTokens(questionMessage.getModelName()) - questionMessage.getPromptTokens() - 1;
        // 获取 maxTokens
        DataWrapper maxTokensDataWrapper = roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.MAX_TOKENS);
        // 如果 maxTokens 为空或者大于当前剩余的 maxTokens
        if (maxTokensDataWrapper.isNull() || maxTokensDataWrapper.asInt() > currentRemainMaxTokens) {
            finalMaxTokens = currentRemainMaxTokens;
        } else {
            finalMaxTokens = maxTokensDataWrapper.asInt();
        }

        // 构建聊天参数
        return ChatCompletion.builder()
                // 最大的 tokens = 模型的最大上线 - 本次 prompt 消耗的 tokens
                .maxTokens(finalMaxTokens)
                .model(questionMessage.getModelName())
                // [0, 2] 越低越精准
                .temperature(roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.TEMPERATURE).asBigDecimal().doubleValue())
                .topP(1.0)
                // 每次生成一条
                .n(1)
                .messages(contentMessages)
                .stream(true)
                .build();
    }

    /**
     * 构建上下文消息
     *
     * @param questionMessage      当前消息
     * @param roomConfigParamAsMap 房间配置参数
     */
    public static LinkedList<Message> buildContextMessage(RoomChatGlmMsgDO questionMessage, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        LinkedList<Message> contextMessages = new LinkedList<>();

        // 上下文条数
        int contextCount = roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.CONTEXT_COUNT).asInt();
        // 不关联上下文，构建当前消息就直接返回
        if (contextCount == 0) {
            contextMessages.add(Message.builder()
                    .role(Message.Role.USER)
                    .content(questionMessage.getContent())
                    .build());
            return contextMessages;
        }

        // 上下文关联时间
        int relatedTimeHour = roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.CONTEXT_RELATED_TIME_HOUR).asInt();

        // 查询上下文消息
        List<RoomChatGlmMsgDO> historyMessages = SpringUtil.getBean(RoomChatGlmMsgService.class).list(new LambdaQueryWrapper<RoomChatGlmMsgDO>()
                // 查询需要的字段
                .select(RoomChatGlmMsgDO::getMessageType, RoomChatGlmMsgDO::getContent)
                // 当前房间
                .eq(RoomChatGlmMsgDO::getRoomId, questionMessage.getRoomId())
                // 查询消息为成功的
                .eq(RoomChatGlmMsgDO::getStatus, RoomChatGlmMsgStatusEnum.COMPLETE_SUCCESS)
                // 上下文的时间范围
                .gt(relatedTimeHour > 0, RoomChatGlmMsgDO::getCreateTime, DateUtil.offsetHour(new Date(), -relatedTimeHour))
                // 限制上下文条数
                .last("limit " + contextCount)
                // 按主键降序
                .orderByDesc(RoomChatGlmMsgDO::getId));
        // 这里降序用来取出最新的上下文消息，然后再反转
        Collections.reverse(historyMessages);
        for (RoomChatGlmMsgDO historyMessage : historyMessages) {
            Message.Role role = (historyMessage.getMessageType() == MessageTypeEnum.ANSWER) ? Message.Role.ASSISTANT : Message.Role.USER;
            contextMessages.add(Message.builder()
                    .role(role)
                    .content(historyMessage.getContent())
                    .build());
        }

        // 查询当前用户消息
        contextMessages.add(Message.builder()
                .role(Message.Role.USER)
                .content(questionMessage.getContent())
                .build());

        return contextMessages;
    }
}
