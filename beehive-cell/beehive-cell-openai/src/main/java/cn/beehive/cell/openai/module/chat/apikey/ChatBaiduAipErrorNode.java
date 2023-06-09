package cn.beehive.cell.openai.module.chat.apikey;

import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import cn.beehive.base.resource.aip.BaiduAipHandler;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/10
 * 百度内容审核错误节点
 */
@Component
public class ChatBaiduAipErrorNode implements ChatErrorNode {

    @Override
    public Pair<Boolean, String> doHandle(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        // 百度内容审核
        BaiduAipHandler baiduAipHandler = SpringUtil.getBean(BaiduAipHandler.class);

        // 获取系统消息
        String systemMessage = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.SYSTEM_MESSAGE).asString();
        // 拼接系统消息
        Pair<Boolean, String> checkTextPassPair = baiduAipHandler.isCheckTextPass(String.valueOf(questionMessage.getId()), systemMessage.concat(questionMessage.getContent()));
        if (checkTextPassPair.getKey()) {
            return new Pair<>(true, null);
        }

        // 审核失败状态
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.CONTENT_CHECK_FAILURE);
        questionMessage.setResponseErrorData(checkTextPassPair.getValue());

        return new Pair<>(false, "系统消息或发送消息内容不符合规范，请修改后重新发送");
    }
}

