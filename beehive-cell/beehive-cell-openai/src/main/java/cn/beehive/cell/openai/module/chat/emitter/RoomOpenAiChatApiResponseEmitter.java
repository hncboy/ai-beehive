package cn.beehive.cell.openai.module.chat.emitter;

import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.base.enums.ApiKeyTokenLimiterEnum;
import cn.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.handler.response.R;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.base.util.OkHttpClientUtil;
import cn.beehive.base.util.ResponseBodyEmitterUtil;
import cn.beehive.base.util.WebUtil;
import cn.beehive.cell.base.hander.strategy.CellConfigStrategy;
import cn.beehive.cell.base.hander.strategy.DataWrapper;
import cn.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import cn.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import cn.beehive.cell.openai.module.chat.listener.ConsoleStreamListener;
import cn.beehive.cell.openai.module.chat.listener.ParsedEventSourceListener;
import cn.beehive.cell.openai.module.chat.listener.ResponseBodyEmitterStreamListener;
import cn.beehive.cell.openai.module.chat.parser.ChatCompletionResponseParser;
import cn.beehive.cell.openai.module.chat.storage.ApiKeyDatabaseDataStorage;
import cn.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        // 初始化问题消息
        RoomOpenAiChatMsgDO questionMessage = initQuestionMessage(sendRequest, roomConfigParamAsMap);

        // 构建上下文消息
        LinkedList<Message> contentMessages = buildContextMessage(questionMessage, roomConfigParamAsMap);
        // 获取上下文的 token 数量设置 promptTokens
        questionMessage.setPromptTokens(TikTokensUtil.tokens(questionMessage.getModelName(), contentMessages));

        // 构建聊天对话请求参数
        ChatCompletion chatCompletion = buildChatCompletion(questionMessage, roomConfigParamAsMap, contentMessages);
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(chatCompletion));

        // 检查 tokenCount 是否超出当前模型的 Token 数量限制
        boolean isExcelledModelTokenLimit = exceedModelTokenLimit(questionMessage, questionMessage.getModelName(), emitter);
        // 保存问题消息
        roomOpenAiChatMsgService.save(questionMessage);
        if (isExcelledModelTokenLimit) {
            return;
        }

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setQuestionMessageDO(questionMessage)
                .build();

        // 构建 OpenAiStreamClient
        OpenAiStreamClient openAiStreamClient = OpenAiStreamClient.builder()
                .okHttpClient(OkHttpClientUtil.getInstance())
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
    public RoomOpenAiChatMsgDO initQuestionMessage(RoomOpenAiChatSendRequest sendRequest, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
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
     * 构建聊天对话请求参数
     *
     * @param questionMessage      问题消息
     * @param roomConfigParamAsMap 房间配置参数
     * @param contentMessages      上下文消息
     * @return 聊天对话请求参数
     */
    private ChatCompletion buildChatCompletion(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap, LinkedList<Message> contentMessages) {
        // 最终的 maxTokens
        int finalMaxTokens;
        // 本次对话剩余的 maxTokens 最大值 = 模型的最大上限 - 本次 prompt 消耗的 tokens - 1
        int currentRemainMaxTokens = ApiKeyTokenLimiterEnum.getTokenLimitByOuterJarModelName(questionMessage.getModelName()) - questionMessage.getPromptTokens() - 1;
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
     * 检查上下文消息的 Token 数是否超出模型限制
     *
     * @param questionMessage 问题消息
     * @param modelName       当前使用的模型名称
     * @param emitter         ResponseBodyEmitter
     */
    private boolean exceedModelTokenLimit(RoomOpenAiChatMsgDO questionMessage, String modelName, ResponseBodyEmitter emitter) {
        Integer promptTokens = questionMessage.getPromptTokens();
        // 当前模型最大 tokens
        int maxTokens = ApiKeyTokenLimiterEnum.getTokenLimitByOuterJarModelName(modelName);

        boolean isExcelledModelTokenLimit = false;

        String msg = null;
        // 判断 token 数量是否超过限制
        if (ApiKeyTokenLimiterEnum.exceedsLimit(modelName, promptTokens)) {
            isExcelledModelTokenLimit = true;

            // 获取当前 prompt 消耗的 tokens
            int currentPromptTokens = TikTokensUtil.tokens(modelName, questionMessage.getContent());
            // 判断历史上下文是否超过限制
            int remainingTokens = promptTokens - currentPromptTokens;
            if (ApiKeyTokenLimiterEnum.exceedsLimit(modelName, remainingTokens)) {
                msg = "当前上下文字数已经达到上限，请减少上下文关联的条数";
            } else {
                msg = StrUtil.format("当前上下文 Token 数量：{}，超过上限：{}，请减少字数发送或减少上下文关联的条数", promptTokens, maxTokens);
            }
        }
        // 剩余的 token 太少也直返返回异常信息
        else if (maxTokens - promptTokens <= 10) {
            isExcelledModelTokenLimit = true;
            msg = "当前上下文字数不足以连续对话，请减少上下文关联的条数";
        }

        if (!isExcelledModelTokenLimit) {
            return false;
        }

        // 超过限制次数更新问题消息
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.EXCEPTION_TOKEN_EXCEED_LIMIT);
        questionMessage.setResponseErrorData(msg);

        // 发送错误消息
        ResponseBodyEmitterUtil.sendWithComplete(emitter, R.fail(msg));

        return true;
    }

    /**
     * 构建上下文消息
     *
     * @param questionMessage      当前消息
     * @param roomConfigParamAsMap 房间配置参数
     */
    private LinkedList<Message> buildContextMessage(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
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
        DataWrapper relatedTimeHourDataWrapper = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.CONTEXT_RELATED_TIME_HOUR);

        // 查询上下文消息
        List<RoomOpenAiChatMsgDO> historyMessages = roomOpenAiChatMsgService.list(new LambdaQueryWrapper<RoomOpenAiChatMsgDO>()
                // 查询需要的字段
                .select(RoomOpenAiChatMsgDO::getMessageType, RoomOpenAiChatMsgDO::getContent)
                // 当前房间
                .eq(RoomOpenAiChatMsgDO::getRoomId, questionMessage.getRoomId())
                // 查询消息为成功的
                .eq(RoomOpenAiChatMsgDO::getStatus, RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS)
                // 上下文的时间范围
                .gt(relatedTimeHourDataWrapper.nonNull(), RoomOpenAiChatMsgDO::getCreateTime, DateUtil.offsetHour(new Date(), -relatedTimeHourDataWrapper.asInt()))
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
