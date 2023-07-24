package com.hncboy.beehive.cell.wxqf.service;

import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息业务接口
 */
public interface RoomWxqfChatMsgService extends IBeehiveService<RoomWxqfChatMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomWxqfChatMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 发送消息
     *
     * @param sendRequest 请求参数
     * @return 响应参数
     */
    ResponseBodyEmitter send(RoomWxqfChatSendRequest sendRequest);
}
