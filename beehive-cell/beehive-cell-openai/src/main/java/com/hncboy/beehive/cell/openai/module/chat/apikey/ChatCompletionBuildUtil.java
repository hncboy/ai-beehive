package com.hncboy.beehive.cell.openai.module.chat.apikey;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatApiModelEnum;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/6/10
 * OpenAi 对话消息构建工具
 */
public class ChatCompletionBuildUtil {

    /**
     * 构建聊天对话请求参数
     *
     * @param questionMessage      问题消息
     * @param roomConfigParamAsMap 房间配置参数
     * @param contentMessages      上下文消息
     * @return 聊天对话请求参数
     */
    public static ChatCompletion buildChatCompletion(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap, LinkedList<Message> contentMessages) {
        // 最终的 maxTokens
        int finalMaxTokens;
        // 本次对话剩余的 maxTokens 最大值 = 模型的最大上限 - 本次 prompt 消耗的 tokens - 1
        int currentRemainMaxTokens = OpenAiChatApiModelEnum.maxTokens(questionMessage.getModelName()) - questionMessage.getPromptTokens() - 1;
        // 获取 maxTokens
        DataWrapper maxTokensDataWrapper = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.MAX_TOKENS);
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
                .temperature(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.TEMPERATURE).asBigDecimal().doubleValue())
                .topP(1.0)
                // 每次生成一条
                .n(1)
                .presencePenalty(roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.PRESENCE_PENALTY).asBigDecimal().doubleValue())
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
    public static LinkedList<Message> buildContextMessage(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        LinkedList<Message> contextMessages = new LinkedList<>();

        // 系统角色消息
        DataWrapper systemMessageDataWrapper = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.SYSTEM_MESSAGE);
        if (Objects.nonNull(systemMessageDataWrapper) && StrUtil.isNotBlank(systemMessageDataWrapper.asString())) {
            Message systemMessage = Message.builder()
                    .role(Message.Role.SYSTEM)
                    .content(systemMessageDataWrapper.asString())
                    .build();
            contextMessages.add(systemMessage);
        }

        // 上下文条数
        int contextCount = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.CONTEXT_COUNT).asInt();
        // 不关联上下文，构建当前消息就直接返回
        if (contextCount == 0) {
            contextMessages.add(Message.builder()
                    .role(Message.Role.USER)
                    .content(questionMessage.getContent())
                    .build());
            return contextMessages;
        }

        // 上下文关联时间
        int relatedTimeHour = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.CONTEXT_RELATED_TIME_HOUR).asInt();

        // 查询上下文消息
        List<RoomOpenAiChatMsgDO> historyMessages = SpringUtil.getBean(RoomOpenAiChatMsgService.class).list(new LambdaQueryWrapper<RoomOpenAiChatMsgDO>()
                // 查询需要的字段
                .select(RoomOpenAiChatMsgDO::getMessageType, RoomOpenAiChatMsgDO::getContent)
                // 当前房间
                .eq(RoomOpenAiChatMsgDO::getRoomId, questionMessage.getRoomId())
                // 查询消息为成功的
                .eq(RoomOpenAiChatMsgDO::getStatus, RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS)
                // 上下文的时间范围
                .gt(relatedTimeHour > 0, RoomOpenAiChatMsgDO::getCreateTime, DateUtil.offsetHour(new Date(), -relatedTimeHour))
                // 限制上下文条数
                .last("limit " + contextCount)
                // 按主键降序
                .orderByDesc(RoomOpenAiChatMsgDO::getId));
        // 这里降序用来取出最新的上下文消息，然后再反转
        Collections.reverse(historyMessages);
        for (RoomOpenAiChatMsgDO historyMessage : historyMessages) {
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
