package com.hncboy.beehive.cell.midjourney.service;

import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.mp.IBeehiveService;
import com.hncboy.beehive.cell.midjourney.domain.request.MjConvertRequest;
import com.hncboy.beehive.cell.midjourney.domain.request.MjDescribeRequest;
import com.hncboy.beehive.cell.midjourney.domain.request.MjImagineRequest;
import com.hncboy.beehive.cell.midjourney.domain.vo.RoomMidjourneyMsgVO;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息业务接口
 */
public interface RoomMidjourneyMsgService extends IBeehiveService<RoomMidjourneyMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomMidjourneyMsgVO> list(RoomMsgCursorQuery cursorQuery);

    /**
     * 查询消息详情
     *
     * @param msgId 消息 id
     * @return 消息详情
     */
    RoomMidjourneyMsgVO detail(Long msgId);

    /**
     * 根据描述创建图像
     *
     * @param imagineRequest 请求参数
     */
    void imagine(MjImagineRequest imagineRequest);

    /**
     * u 转换
     *
     * @param convertRequest 请求参数
     */
    void upscale(MjConvertRequest convertRequest);

    /**
     * v 转换
     *
     * @param convertRequest 请求参数
     */
    void variation(MjConvertRequest convertRequest);

    /**
     * 根据图片生成描述
     *
     * @param describeRequest 请求参数
     */
    void describe(MjDescribeRequest describeRequest);
}
