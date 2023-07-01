package com.hncboy.beehive.cell.midjourney.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.mapper.RoomMidjourneyMsgMapper;
import com.hncboy.beehive.cell.midjourney.handler.MidjourneyTaskQueueHandler;
import com.hncboy.beehive.cell.midjourney.service.AdminRoomMidjourneyMsgService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/7/1
 * 管理端-Midjourney 房间消息业务接口实现类
 */
@Slf4j
@Service
public class AdminRoomMidjourneyMsgServiceImpl extends ServiceImpl<RoomMidjourneyMsgMapper, RoomMidjourneyMsgDO> implements AdminRoomMidjourneyMsgService {

    @Resource
    private MidjourneyTaskQueueHandler midjourneyTaskQueueHandler;

    @Override
    public void markErrorMessage(Long msgId) {
        RoomMidjourneyMsgDO roomMidjourneyMsgDO = getById(msgId);
        MidjourneyMsgStatusEnum status = roomMidjourneyMsgDO.getStatus();
        if (status == MidjourneyMsgStatusEnum.MJ_FAILURE) {
            boolean update = update(new RoomMidjourneyMsgDO(), new LambdaUpdateWrapper<RoomMidjourneyMsgDO>()
                    .set(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.SYS_FINISH_MJ_IN_PROGRESS_FAILURE)
                    .set(RoomMidjourneyMsgDO::getResponseContent, "系统异常，Midjourney 消息未全部接收到")
                    .set(RoomMidjourneyMsgDO::getFailureReason, StrUtil.format("系统异常，Midjourney 消息未全部接收到，由管理员手动 {} 处理", DateUtil.now()))
                    .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.MJ_IN_PROGRESS)
                    // 防止已经被更新过
                    .eq(RoomMidjourneyMsgDO::getUpdateTime, roomMidjourneyMsgDO.getUpdateTime())
                    .eq(RoomMidjourneyMsgDO::getId, msgId));
            log.info("Midjourney 手动清理过期的任务，更新状态为 SYS_FINISH_MJ_IN_PROGRESS_FAILURE，消息 id：{}，更新结果：{}", msgId, update);
            if (update) {
                // 更新成功的话结束这个执行任务
                midjourneyTaskQueueHandler.finishExecuteTask(msgId);
            }
        }

        if (status == MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED) {
            boolean update = update(new RoomMidjourneyMsgDO(), new LambdaUpdateWrapper<RoomMidjourneyMsgDO>()
                    .set(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE)
                    .set(RoomMidjourneyMsgDO::getResponseContent, "系统异常，未接收到 Midjourney 初始化消息")
                    .set(RoomMidjourneyMsgDO::getFailureReason, StrUtil.format("系统异常，未接收到 Midjourney 初始化消息，由管理员手动 {} 处理", DateUtil.now()))
                    .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED)
                    // 防止已经被更新过
                    .eq(RoomMidjourneyMsgDO::getUpdateTime, roomMidjourneyMsgDO.getUpdateTime())
                    .eq(RoomMidjourneyMsgDO::getId, msgId));
            log.info("Midjourney 定时任务，清理过期的任务，更新状态为 SYS_WAIT_MJ_RECEIVED_FAILURE，消息 id：{}，更新结果：{}", msgId, update);

            if (update) {
                // 更新成功的话结束这个执行任务
                midjourneyTaskQueueHandler.finishExecuteTask(msgId);
            }
        }
    }
}
