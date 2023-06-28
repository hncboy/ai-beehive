package com.hncboy.beehive.cell.openai.module.chat.emitter;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import cn.hutool.extra.spring.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间消息响应转 Emitter 分发器
 */
public class RoomOpenAiChatResponseEmitterDispatcher {

    /**
     * 响应处理器
     */
    public static final Map<CellCodeEnum, RoomOpenAiChatResponseEmitter> RESPONSE_EMITTER_MAP = new HashMap<>() {{
        put(CellCodeEnum.OPENAI_CHAT_API_3_5, SpringUtil.getBean(RoomOpenAiChatApiResponseEmitter.class));
        put(CellCodeEnum.OPENAI_CHAT_API_4, SpringUtil.getBean(RoomOpenAiChatApiResponseEmitter.class));
        put(CellCodeEnum.OPENAI_CHAT_WEB_3_5, SpringUtil.getBean(RoomOpenAiChatWebResponseEmitter.class));
        put(CellCodeEnum.OPENAI_CHAT_WEB_4, SpringUtil.getBean(RoomOpenAiChatWebResponseEmitter.class));
    }};

    /**
     * 根据 cellCodeEnum 获取对应的响应处理器
     *
     * @param cellCodeEnum cellCodeEnum
     * @return 响应处理器
     */
    public static RoomOpenAiChatResponseEmitter doDispatch(CellCodeEnum cellCodeEnum) {
        return RESPONSE_EMITTER_MAP.get(cellCodeEnum);
    }
}