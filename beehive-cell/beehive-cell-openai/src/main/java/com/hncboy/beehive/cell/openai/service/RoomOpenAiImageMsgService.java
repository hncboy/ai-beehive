package com.hncboy.beehive.cell.openai.service;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiImageMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiImageSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiImageMsgVO;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像房间消息业务接口
 */
public interface RoomOpenAiImageMsgService extends IBeehiveService<RoomOpenAiImageMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomOpenAiImageMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 发送消息
     *
     * @param sendRequest 请求参数
     * @return 响应参数
     */
    RoomOpenAiImageMsgVO send(RoomOpenAiImageSendRequest sendRequest);
}
