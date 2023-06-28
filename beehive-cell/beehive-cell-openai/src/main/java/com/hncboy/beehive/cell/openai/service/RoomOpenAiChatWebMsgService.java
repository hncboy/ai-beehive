package com.hncboy.beehive.cell.openai.service;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatWebCellConfigCodeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/1
 * OpenAi 对话 Web 房间消息业务接口
 */
public interface RoomOpenAiChatWebMsgService extends IBeehiveService<RoomOpenAiChatWebMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomOpenAiChatMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 初始化问题消息
     *
     * @param sendRequest 请求参数
     * @param roomConfigParamAsMap 房间配置参数
     * @return 问题消息
     */
    RoomOpenAiChatWebMsgDO initQuestionMessage(RoomOpenAiChatSendRequest sendRequest, Map<OpenAiChatWebCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap);
}
