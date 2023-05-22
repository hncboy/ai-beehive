package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.base.util.RedisUtil;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.service.DiscordService;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.lock.annotation.Lock4j;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author hncboy
 * @date 2023/5/19
 * Midjourney 任务队列处理器
 */
@Slf4j
@Component
public class MjTaskQueueHandler {

    @Resource
    private MidjourneyConfig midjourneyConfig;

    private static final String PREFIX = "cell:mj:";

    /**
     * 等待队列 key
     */
    private static final String WAIT_QUEUE_KEY = PREFIX + "wait";

    /**
     * 执行任务的数量 key
     */
    private static final String EXECUTE_TASK_COUNT_KEY = PREFIX + "execute:count";

    /**
     * 执行任务 key 前綴
     * TODO 监听过期，用来减少 EXECUTE_TASK_COUNT_KEY
     */
    private static final String PREFIX_EXECUTE_TASK_KEY = PREFIX + "execute:task:";

    /**
     * 用于分布式锁的 key
     */
    private static final String TASK_LOCK_KEY = PREFIX + "lock";

    /**
     * 插入新任务
     * 整个方法加锁，防止多个线程同时插入等待队列或执行中操作
     *
     * @param mjMsgId 消息 id
     * @return 是否插入成功
     */
    @Lock4j(name = TASK_LOCK_KEY, expire = 60000, acquireTimeout = 3000)
    public MjMsgStatusEnum pushNewTask(Long mjMsgId) {
        // 获取等待队列长度
        Long currentWaitQueueLength = RedisUtil.lLen(WAIT_QUEUE_KEY);
        // 等待队列已满
        if (currentWaitQueueLength >= midjourneyConfig.getMaxWaitQueueSize()) {
            return MjMsgStatusEnum.SYS_MAX_QUEUE;
        }

        // 获取执行中任务的数量
        int executeTaskCount = getExecuteTaskCount();

        // 队列中有任务 或者 执行任务的数量大于最大执行任务数量 就得排队
        if (currentWaitQueueLength > 0 || executeTaskCount >= midjourneyConfig.getMaxExecuteQueueSize()) {
            log.info("Midjourney 进入等待队列消息：{}", mjMsgId);

            // 插入队列首部
            RedisUtil.lLeftPush(WAIT_QUEUE_KEY, String.valueOf(mjMsgId));
            // 队列过期时间重置为 1 小时
            RedisUtil.expire(WAIT_QUEUE_KEY, 1, TimeUnit.HOURS);
            return MjMsgStatusEnum.SYS_QUEUING;
        }

        // 设置执行中的任务
        setExecuteTask(executeTaskCount, mjMsgId);
        return MjMsgStatusEnum.MJ_WAIT_RECEIVED;
    }

    /**
     * 结束执行中任务
     *
     * @param mjMsgId 消息 id
     */
    @Lock4j(name = TASK_LOCK_KEY, expire = 60000, acquireTimeout = 3000)
    public void finishExecuteTask(Long mjMsgId) {
        // 删除执行中的任务 key
        Boolean deleteResult = RedisUtil.delete(PREFIX_EXECUTE_TASK_KEY + mjMsgId);
        // 获取执行任务数量
        int executeTaskCount = getExecuteTaskCount();
        if (deleteResult) {
            // 如果执行任务数量大于 0，就减 1
            if (executeTaskCount > 0) {
                executeTaskCount--;
                // 执行任务数量 - 1
                RedisUtil.set(EXECUTE_TASK_COUNT_KEY, String.valueOf(executeTaskCount), 30, TimeUnit.MINUTES);
            }
        }

        // 拉取新任务
        pullTaskFromWaitQueue(midjourneyConfig.getMaxExecuteQueueSize() - executeTaskCount);
    }

    /**
     * 校验并拉取任务
     */
    @Lock4j(name = TASK_LOCK_KEY, expire = 60000, acquireTimeout = 3000)
    public void checkAndPullTask() {
        // 获取执行中任务的数量
        int executeTaskCount = getExecuteTaskCount();

        // 执行任务的数量大于最大执行任务数量则返回
        if (executeTaskCount >= midjourneyConfig.getMaxExecuteQueueSize()) {
            return;
        }

        // 拉取新任务
        pullTaskFromWaitQueue(midjourneyConfig.getMaxExecuteQueueSize() - executeTaskCount);
    }

    /**
     * 从等待队列中拉取任务
     * 把能拉取的任务拉取出来，进入执行中
     * @param canPullCount 可以拉取的数量
     */
    private void pullTaskFromWaitQueue(int canPullCount) {
        for (int i = 0; i < canPullCount; i++) {
            // 从等待队列中获取任务
            String newMjMsgIdStr = RedisUtil.lLeftPop(WAIT_QUEUE_KEY);
            // 为空说明没有待执行的任务
            if (Objects.isNull(newMjMsgIdStr)) {
                return;
            }

            // 获取新任务的消息数据
            RoomMjMsgService roomMjMsgService = SpringUtil.getBean(RoomMjMsgService.class);
            RoomMjMsgDO newRoomMjMsgDO = roomMjMsgService.getById(newMjMsgIdStr);
            // 为空说明是异常数据
            if (Objects.isNull(newRoomMjMsgDO)) {
                return;
            }

            log.info("Midjourney 从队列中拉取到新任务：{}", newRoomMjMsgDO.getId());

            // 设置执行中的任务
            setExecuteTask(getExecuteTaskCount(), newRoomMjMsgDO.getId());

            // 调用 imagine 接口
            DiscordService discordService = SpringUtil.getBean(DiscordService.class);
            Pair<Boolean, String> imagineResultPair = discordService.imagine(newRoomMjMsgDO.getFinalPrompt());
            // 调用失败的情况，应该是少数情况，这里不重试
            if (!imagineResultPair.getKey()) {
                newRoomMjMsgDO.setStatus(MjMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE);
                newRoomMjMsgDO.setResponseContent("系统异常，排队中调用 imagine 接口失败，请稍后再试");
                newRoomMjMsgDO.setFailureReason(imagineResultPair.getValue());
            } else {
                newRoomMjMsgDO.setStatus(MjMsgStatusEnum.MJ_WAIT_RECEIVED);
            }
            roomMjMsgService.updateById(newRoomMjMsgDO);
        }
    }

    /**
     * 设置执行中的任务
     *
     * @param executeTaskCount 执行任务数量
     * @param mjMsgId          消息 id
     */
    private void setExecuteTask(int executeTaskCount, Long mjMsgId) {
        log.info("Midjourney 设置执行中的任务：{}", mjMsgId);

        // 执行任务数量 + 1，过期时间 30 分钟，每次有新任务都会重置过期时间，保证 discord 响应失败时可以清除执行任务数量
        RedisUtil.set(EXECUTE_TASK_COUNT_KEY, String.valueOf(executeTaskCount + 1), 30, TimeUnit.MINUTES);
        // 插入执行中任务 key，过期时间 5 分钟，正常情况没问题
        RedisUtil.set(PREFIX_EXECUTE_TASK_KEY + mjMsgId, String.valueOf(mjMsgId), 5, TimeUnit.MINUTES);
    }

    /**
     * 获取执行中的任务数量
     *
     * @return 执行的任务数量
     */
    private int getExecuteTaskCount() {
        String executeTaskCountStr = RedisUtil.get(EXECUTE_TASK_COUNT_KEY);
        return executeTaskCountStr == null ? 0 : Integer.parseInt(executeTaskCountStr);
    }
}
