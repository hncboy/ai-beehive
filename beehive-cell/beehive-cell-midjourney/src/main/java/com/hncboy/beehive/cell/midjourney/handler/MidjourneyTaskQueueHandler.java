package com.hncboy.beehive.cell.midjourney.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.util.RedisUtil;
import com.hncboy.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import com.hncboy.beehive.cell.midjourney.service.DiscordService;
import com.hncboy.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/19
 * Midjourney 任务队列处理器
 */
@Slf4j
@Component
public class MidjourneyTaskQueueHandler {

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
     */
    public static final String PREFIX_EXECUTE_TASK_KEY = PREFIX + "execute:task:";

    /**
     * 用于分布式锁的 key
     */
    private static final String TASK_LOCK_KEY = PREFIX + "lock";

    /**
     * 插入新任务
     * 整个方法加锁，防止多个线程同时插入等待队列或执行中操作
     *
     * @param midjourneyMsgId      消息 id
     * @param midjourneyProperties Midjourney 配置
     * @return 消息状态 {@link MidjourneyMsgStatusEnum}
     * 返回值包含三种状态：SYS_MAX_QUEUE、SYS_QUEUING、MJ_WAIT_RECEIVED
     */
    @Lock4j(name = TASK_LOCK_KEY, expire = 60000, acquireTimeout = 3000)
    public MidjourneyMsgStatusEnum pushNewTask(Long midjourneyMsgId, MidjourneyProperties midjourneyProperties) {
        // 获取等待队列长度
        int currentWaitQueueLength = getWaitQueueLength();
        // 等待队列已满
        if (currentWaitQueueLength >= midjourneyProperties.getMaxWaitQueueSize()) {
            return MidjourneyMsgStatusEnum.SYS_MAX_QUEUE;
        }

        // 获取执行中任务的数量
        int executeTaskCount = getExecuteTaskCount();

        // 队列中有任务 或者 执行任务的数量大于最大执行任务数量 就得排队
        if (currentWaitQueueLength > 0 || executeTaskCount >= midjourneyProperties.getMaxExecuteQueueSize()) {
            log.info("Midjourney 进入等待队列消息：{}", midjourneyMsgId);

            // 插入队列尾部
            RedisUtil.lRightPush(WAIT_QUEUE_KEY, String.valueOf(midjourneyMsgId));
            // 队列过期时间重置为 6 小时
            RedisUtil.expire(WAIT_QUEUE_KEY, 6, TimeUnit.HOURS);
            return MidjourneyMsgStatusEnum.SYS_QUEUING;
        }

        // 设置执行中的任务
        setExecuteTask(executeTaskCount, midjourneyMsgId);
        return MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED;
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

        // 获取执行中的任务
        int executeTaskCount = getExecuteTaskCount();
        // 删除成功的情况下
        if (deleteResult) {
            // 如果执行任务数量大于 0，就减 1
            if (executeTaskCount > 0) {
                executeTaskCount--;
                // 执行任务数量 - 1，永不过期
                RedisUtil.set(EXECUTE_TASK_COUNT_KEY, String.valueOf(executeTaskCount));
            }
        }

        MidjourneyProperties midjourneyProperties = MidjourneyProperties.init();
        // 拉取新任务
        pullTaskFromWaitQueue(midjourneyProperties, executeTaskCount);
    }

    /**
     * 校验并拉取任务
     */
    @Lock4j(name = TASK_LOCK_KEY, expire = 60000, acquireTimeout = 3000)
    public void checkAndPullTask() {
        MidjourneyProperties midjourneyProperties = MidjourneyProperties.init();

        // 获取执行中任务的数量
        int executeTaskCount = getExecuteTaskCount();

        // 执行任务的数量大于最大执行任务数量则返回
        if (executeTaskCount >= midjourneyProperties.getMaxExecuteQueueSize()) {
            return;
        }

        // 拉取新任务
        pullTaskFromWaitQueue(midjourneyProperties, executeTaskCount);
    }

    /**
     * 从等待队列中拉取任务
     * 把能拉取的任务拉取出来，进入执行中
     *
     * @param midjourneyProperties 可以拉取的数量
     * @param executeTaskCount     执行中的任务数量
     */
    private void pullTaskFromWaitQueue(MidjourneyProperties midjourneyProperties, int executeTaskCount) {
        // 可以拉取的数量
        int canPullCount = midjourneyProperties.getMaxExecuteQueueSize() - executeTaskCount;
        for (int i = 0; i < canPullCount; i++) {
            // 从等待队列中获取任务
            String newMjMsgIdStr = RedisUtil.lLeftPop(WAIT_QUEUE_KEY);
            // 为空说明没有待执行的任务
            if (Objects.isNull(newMjMsgIdStr)) {
                return;
            }

            // 获取新任务的消息数据
            RoomMidjourneyMsgService roomMidjourneyMsgService = SpringUtil.getBean(RoomMidjourneyMsgService.class);
            RoomMidjourneyMsgDO newAnswerMessage = roomMidjourneyMsgService.getById(newMjMsgIdStr);
            // 为空 或 不为回答 或 状态不为排队中 就跳过
            if (Objects.isNull(newAnswerMessage) || newAnswerMessage.getType() != MessageTypeEnum.ANSWER || newAnswerMessage.getStatus() != MidjourneyMsgStatusEnum.SYS_QUEUING) {
                return;
            }

            // 此时 Redis 队列中无数据，但是消息状态是排队中，下面报错会导致队列状态一直处于排队中

            log.info("Midjourney 从队列中拉取到新任务：{}", newAnswerMessage.getId());

            Pair<Boolean, String> resultPair = new Pair<>(false, "初始化");

            DiscordService discordService = SpringUtil.getBean(DiscordService.class);
            switch (newAnswerMessage.getAction()) {
                case IMAGINE ->
                        resultPair = discordService.imagine(newAnswerMessage.getFinalPrompt(), midjourneyProperties);
                case DESCRIBE ->
                        resultPair = discordService.describe(newAnswerMessage.getDiscordImageUrl(), midjourneyProperties);
                case VARIATION -> {
                    // 不考虑频道切换
                    RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO = roomMidjourneyMsgService.getById(newAnswerMessage.getUvParentId());
                    resultPair = discordService.variation(parentRoomMidjourneyMsgDO.getDiscordMessageId(), newAnswerMessage.getUvIndex(),
                            MidjourneyRoomMsgHandler.getDiscordMessageHash(parentRoomMidjourneyMsgDO.getDiscordImageUrl()), midjourneyProperties);
                }
                case UPSCALE -> {
                    // 不考虑频道切换
                    RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO = roomMidjourneyMsgService.getById(newAnswerMessage.getUvParentId());
                    resultPair = discordService.upscale(parentRoomMidjourneyMsgDO.getDiscordMessageId(), newAnswerMessage.getUvIndex(),
                            MidjourneyRoomMsgHandler.getDiscordMessageHash(parentRoomMidjourneyMsgDO.getDiscordImageUrl()), midjourneyProperties);
                }
            }

            // 是否执行
            boolean isExecute = false;

            // 调用失败的情况，应该是少数情况，这里不重试
            if (!resultPair.getKey()) {
                newAnswerMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE);
                newAnswerMessage.setResponseContent("系统异常，排队中调用接口失败，本次任务终止");
                newAnswerMessage.setFailureReason(resultPair.getValue());
            } else {
                newAnswerMessage.setStatus(MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED);
                isExecute = true;
            }
            // 更新消息状态
            boolean update = roomMidjourneyMsgService.update(newAnswerMessage, new LambdaUpdateWrapper<RoomMidjourneyMsgDO>()
                    .eq(RoomMidjourneyMsgDO::getId, newAnswerMessage.getId())
                    // 防止被其他地方修改过
                    .eq(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.SYS_QUEUING));
            if (update && isExecute) {
                // 设置执行中的任务
                setExecuteTask(getExecuteTaskCount(), newAnswerMessage.getId());
            }
        }
    }

    /**
     * 设置执行中的任务
     *
     * @param executeTaskCount 执行任务数量
     * @param midjourneyMsgId  消息 id
     */
    private void setExecuteTask(int executeTaskCount, Long midjourneyMsgId) {
        log.info("Midjourney 设置执行中的任务：{}", midjourneyMsgId);

        // 执行任务数量 + 1，永不过期，有问题自己处理
        RedisUtil.set(EXECUTE_TASK_COUNT_KEY, String.valueOf(executeTaskCount + 1));
        // 插入执行中任务 key，永不过期，有问题自己处理
        RedisUtil.set(PREFIX_EXECUTE_TASK_KEY + midjourneyMsgId, String.valueOf(midjourneyMsgId));
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

    /**
     * 获取等待队列长度
     *
     * @return 等待队列长度
     */
    public int getWaitQueueLength() {
        Long currentWaitQueueLength = RedisUtil.lLen(WAIT_QUEUE_KEY);
        return Math.toIntExact(currentWaitQueueLength == null ? 0 : currentWaitQueueLength);
    }

    /**
     * 获取等待队列元素
     *
     * @return 队列列表
     */
    public List<Long> getWaitQueueElement() {
        List<String> elements = RedisUtil.lRange(WAIT_QUEUE_KEY, 0, -1);
        if (CollectionUtil.isEmpty(elements)) {
            return Collections.emptyList();
        }
        return elements.stream().map(Long::valueOf).collect(Collectors.toList());
    }
}
