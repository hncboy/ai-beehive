package cn.beehive.cell.bing.service;

import cn.beehive.base.domain.entity.RoomBingMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import cn.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间消息业务接口
 */
public interface RoomBingMsgService extends IService<RoomBingMsgDO> {

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
