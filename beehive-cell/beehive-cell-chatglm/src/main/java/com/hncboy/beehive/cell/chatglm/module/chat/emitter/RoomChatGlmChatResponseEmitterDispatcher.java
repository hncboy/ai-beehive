package com.hncboy.beehive.cell.chatglm.module.chat.emitter;

import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.beehive.base.enums.CellCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话房间消息响应转 Emitter 分发器
 */
public class RoomChatGlmChatResponseEmitterDispatcher {

    /**
     * 响应处理器
     */
    public static final Map<CellCodeEnum, RoomChatGlmChatApiResponseEmitter> RESPONSE_EMITTER_MAP = new HashMap<>() {{
        put(CellCodeEnum.CHAT_GLM, SpringUtil.getBean(RoomChatGlmChatApiResponseEmitter.class));
    }};

    /**
     * 根据 cellCodeEnum 获取对应的响应处理器
     *
     * @param cellCodeEnum cellCodeEnum
     * @return 响应处理器
     */
    public static RoomChatGlmChatApiResponseEmitter doDispatch(CellCodeEnum cellCodeEnum) {
        return RESPONSE_EMITTER_MAP.get(cellCodeEnum);
    }
}