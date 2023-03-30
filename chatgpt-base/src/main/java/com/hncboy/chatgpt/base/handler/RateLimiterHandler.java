package com.hncboy.chatgpt.base.handler;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hncboy
 * @date 2023/3/30 21:01
 * 限流处理器
 */
public class RateLimiterHandler {


    /**
     * 存储每个 IP 的请求时间队列
     * TODO 数据需要持久化吗
     */
    public static final Map<String, Deque<LocalDateTime>> REQUEST_TIMESTAMP_MAP = new ConcurrentHashMap<>();

    /**
     * 根据 ip 请求是否被限制请求
     *
     * @param ip ip
     * @return 是否允许
     */
    public static Pair<Boolean, String> allowRequest(String ip) {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        // 最大请求次数小于等于 0 不限流
        if (chatConfig.getMaxRequest() <= 0) {
            return new Pair<>(true, null);
        }

        // 初始化指定 ip 的队列
        Deque<LocalDateTime> timestampDeque = REQUEST_TIMESTAMP_MAP.computeIfAbsent(ip, k -> new ArrayDeque<>());
        LocalDateTime now = LocalDateTime.now();

        // 移除超出滑动窗口的请求时间
        removeExpiredRequests(timestampDeque, now);

        // 检查队列的大小是否小于最大允许的请求数
        if (timestampDeque.size() < chatConfig.getMaxRequest()) {
            timestampDeque.addLast(now);
            return new Pair<>(true, null);
        }

        // 下次预计可以发送的时间
        assert timestampDeque.peekFirst() != null;
        LocalDateTime nextSendTime = timestampDeque.peekFirst().plusSeconds(chatConfig.getMaxRequestSecond());
        return new Pair<>(false, LocalDateTimeUtil.format(nextSendTime, DatePattern.NORM_DATETIME_PATTERN));
    }

    /**
     * 移除超出滑动窗口的请求时间
     *
     * @param timestampDeque 请求时间队列
     * @param now            当前时间
     */
    private static void removeExpiredRequests(Deque<LocalDateTime> timestampDeque, LocalDateTime now) {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        while (!timestampDeque.isEmpty() && now.minusSeconds(chatConfig.getMaxRequestSecond()).isAfter(timestampDeque.peekFirst())) {
            timestampDeque.pollFirst();
        }
    }
}
