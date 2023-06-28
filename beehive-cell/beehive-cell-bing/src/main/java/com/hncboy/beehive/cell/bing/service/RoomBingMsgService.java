package com.hncboy.beehive.cell.bing.service;

import com.hncboy.beehive.base.domain.entity.RoomBingMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.hncboy.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间消息业务接口
 */
public interface RoomBingMsgService extends IBeehiveService<RoomBingMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomBingMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 发送消息
     *
     * @param sendRequest 请求参数
     * @return 响应参数
     */
    ResponseBodyEmitter send(RoomBingMsgSendRequest sendRequest);
}
