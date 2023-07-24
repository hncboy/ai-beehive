package com.hncboy.beehive.cell.wxqf.module.chat.emitter;

import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.beehive.base.enums.CellCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息响应转 Emitter 分发器
 */
public class WxqfChatResponseEmitterDispatcher {

    /**
     * 响应处理器
     */
    public static final Map<CellCodeEnum, AbstractWxqfChatResponseEmitter> RESPONSE_EMITTER_MAP = new HashMap<>() {{
        put(CellCodeEnum.WXQF_ERNIE_BOT, SpringUtil.getBean(WxqfChatErnieBotResponseEmitter.class));
        put(CellCodeEnum.WXQF_ERNIE_BOT_TURBO, SpringUtil.getBean(WxqfChatErnieBotTurboResponseEmitter.class));
        put(CellCodeEnum.WXQF_BLOOMZ_7B, SpringUtil.getBean(WxqfChatBloomz7bResponseEmitter.class));
    }};

    /**
     * 根据 cellCodeEnum 获取对应的响应处理器
     *
     * @param cellCodeEnum cellCodeEnum
     * @return 响应处理器
     */
    public static AbstractWxqfChatResponseEmitter doDispatch(CellCodeEnum cellCodeEnum) {
        return RESPONSE_EMITTER_MAP.get(cellCodeEnum);
    }
}