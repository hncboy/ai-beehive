package com.hncboy.beehive.cell.chatglm.module.chat.api;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.handler.SensitiveWordHandler;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author hanpeng
 * @date 2023/8/4
 * 本地敏感词库校验错误处理节点
 */
@Component
public class ChatGlmLocalSensitiveWordErrorNode implements ChatGlmErrorNode {

    @Override
    public Pair<Boolean, String> doHandle(RoomChatGlmMsgDO questionMessage, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        // 判断是否启用
        boolean enabled = roomConfigParamAsMap.get(ChatGlmCellConfigCodeEnum.ENABLED_LOCAL_SENSITIVE_WORD).asBoolean();
        if (!enabled) {
            return new Pair<>(true, null);
        }

        List<String> userMessageSensitiveWords = SensitiveWordHandler.checkWord(questionMessage.getContent());
        if (CollectionUtil.isEmpty(userMessageSensitiveWords)) {
            return new Pair<>(true, null);
        }

        return new Pair<>(false, StrUtil.format("发送失败，发送消息包含敏感词{}", userMessageSensitiveWords));
    }
}
