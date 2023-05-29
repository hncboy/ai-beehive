package cn.beehive.cell.midjourney.service;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.handler.mp.IBeehiveService;
import cn.beehive.cell.midjourney.domain.request.MjConvertRequest;
import cn.beehive.cell.midjourney.domain.request.MjDescribeRequest;
import cn.beehive.cell.midjourney.domain.request.MjImagineRequest;
import cn.beehive.cell.midjourney.domain.vo.RoomMjMsgVO;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息业务接口
 */
public interface RoomMjMsgService extends IBeehiveService<RoomMjMsgDO> {

    /**
     * 查询消息列表
     *
     * @param cursorQuery 请求参数
     * @return 消息列表
     */
    List<RoomMjMsgVO> list(RoomMsgCursorQuery cursorQuery);

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
