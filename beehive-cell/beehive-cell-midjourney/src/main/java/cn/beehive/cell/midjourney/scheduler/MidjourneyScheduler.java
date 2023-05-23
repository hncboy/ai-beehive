package cn.beehive.cell.midjourney.scheduler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.cell.midjourney.handler.MjTaskQueueHandler;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/20
 * Midjourney 定时任务
 */
@Slf4j
@Component
@DependsOn("discordStarter")
public class MidjourneyScheduler {

    @Resource
    private MjTaskQueueHandler mjTaskQueueHandler;

    @Resource
    private RoomMjMsgService roomMjMsgService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void handlerTask() {
        log.info("Midjourney 定时任务开始");

        /*
         * 因为意外可能导致 Redis 中进行中的任务为空，任务都堆积在队列中
         * 任务一直排队，通过定时任务定时拉取避免这种情况
         */
        mjTaskQueueHandler.checkAndPullTask();

        // 清理过期的任务
        clearHistoryTask();
    }

    /**
     * 清理过期的任务
     */
    private void clearHistoryTask() {
        clearHistoryTaskScene1();
        clearHistoryTaskScene2();
    }

    /**
     * 清理过期的任务
     * 场景 B：消息状态一直 MJ_IN_PROGRESS
     * 情况 1：网络情况，如 MJ 服务挂了或程序突然重启，导致没有收到回调消息
     * 情况 2：收到消息但是程序更新失败，此时算 bug，发现要及时修复
     */
    private void clearHistoryTaskScene2() {
        List<RoomMjMsgDO> roomMessages = roomMjMsgService.list(new LambdaQueryWrapper<RoomMjMsgDO>()
                .eq(RoomMjMsgDO::getStatus, MjMsgStatusEnum.MJ_IN_PROGRESS)
                // 30 分钟前的消息
                .lt(RoomMjMsgDO::getDiscordStartTime, LocalDateTime.now().minusMinutes(30)));
        for (RoomMjMsgDO roomMjMsg : roomMessages) {
            boolean update = roomMjMsgService.update(new RoomMjMsgDO(), new LambdaUpdateWrapper<RoomMjMsgDO>()
                    .set(RoomMjMsgDO::getStatus, MjMsgStatusEnum.SYS_FINISH_MJ_IN_PROGRESS_FAILURE)
                    .set(RoomMjMsgDO::getResponseContent, "系统异常，Midjourney 消息未全部接收到")
                    .set(RoomMjMsgDO::getFailureReason, StrUtil.format("系统异常，Midjourney 消息未全部接收到，由 {} 定时任务处理", DateUtil.now()))
                    .eq(RoomMjMsgDO::getStatus, MjMsgStatusEnum.MJ_IN_PROGRESS)
                    // 防止已经被更新过
                    .eq(RoomMjMsgDO::getUpdateTime, roomMjMsg.getUpdateTime())
                    .eq(RoomMjMsgDO::getId, roomMjMsg.getId()));
            log.info("Midjourney 定时任务，清理过期的任务，更新状态为 SYS_FINISH_MJ_IN_PROGRESS_FAILURE，消息 id：{}，更新结果：{}", roomMjMsg.getId(), update);

            if (update) {
                // 更新成功的话结束这个执行任务
                mjTaskQueueHandler.finishExecuteTask(roomMjMsg.getId());
            }
        }
    }

    /**
     * 清理过期的任务
     * 场景 A: 消息状态一直 MJ_WAIT_RECEIVED
     * 情况 1：网络情况，如 MJ 服务挂了或程序突然重启，导致没有收到回调消息
     * 情况 2：执行 imagine 指令，用户输入违禁词，调用成功无回调（主要）
     * 情况 3：收到回调消息，但是程序更新失败，此时算 bug，发现要及时修复
     * 情况 4：发送消息失败，没有回滚，此时算 bug，发现要及时修复
     * 情况 5：重复执行 upscale 操作，discord 会报错，此时不会有回调（主要）
     */
    private void clearHistoryTaskScene1() {
        List<RoomMjMsgDO> roomMessages = roomMjMsgService.list(new LambdaQueryWrapper<RoomMjMsgDO>()
                .eq(RoomMjMsgDO::getStatus, MjMsgStatusEnum.MJ_WAIT_RECEIVED)
                // 10 分钟前的消息
                .lt(RoomMjMsgDO::getCreateTime, LocalDateTime.now().minusMinutes(10)));
        for (RoomMjMsgDO roomMjMsg : roomMessages) {
            boolean update = roomMjMsgService.update(new RoomMjMsgDO(), new LambdaUpdateWrapper<RoomMjMsgDO>()
                    .set(RoomMjMsgDO::getStatus, MjMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE)
                    .set(RoomMjMsgDO::getResponseContent, "系统异常，未接收到 Midjourney 初始化消息")
                    .set(RoomMjMsgDO::getFailureReason, StrUtil.format("系统异常，未接收到 Midjourney 初始化消息，由 {} 定时任务处理", DateUtil.now()))
                    .eq(RoomMjMsgDO::getStatus, MjMsgStatusEnum.MJ_WAIT_RECEIVED)
                    // 防止已经被更新过
                    .eq(RoomMjMsgDO::getUpdateTime, roomMjMsg.getUpdateTime())
                    .eq(RoomMjMsgDO::getId, roomMjMsg.getId()));
            log.info("Midjourney 定时任务，清理过期的任务，更新状态为 SYS_WAIT_MJ_RECEIVED_FAILURE，消息 id：{}，更新结果：{}", roomMjMsg.getId(), update);

            if (update) {
                // 更新成功的话结束这个执行任务
                mjTaskQueueHandler.finishExecuteTask(roomMjMsg.getId());
            }
        }
    }
}
