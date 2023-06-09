package cn.beehive.cell.openai.service;

import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.handler.mp.IBeehiveService;
import cn.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import cn.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间消息服务层
 */
public interface RoomOpenAiChatMsgService extends IBeehiveService<RoomOpenAiChatMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomOpenAiChatMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 发送消息
     *
     * @param sendRequest 请求参数
     * @return 响应参数
     */
    ResponseBodyEmitter send(RoomOpenAiChatSendRequest sendRequest);
}
