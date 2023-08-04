package com.hncboy.beehive.cell.chatglm.service;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.chatglm.domain.request.RoomChatGlmSendRequest;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hanpeng
 * @date 2023/8/3
 * ChatGLM 对话房间消息业务接口
 */
public interface RoomChatGlmMsgService extends IBeehiveService<RoomChatGlmMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomChatGlmChatMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 发送消息
     *
     * @param sendRequest 请求参数
     * @return 响应参数
     */
    ResponseBodyEmitter send(RoomChatGlmSendRequest sendRequest);
}
