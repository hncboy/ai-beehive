package com.hncboy.chatgpt.admin.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.hncboy.chatgpt.admin.domain.vo.RateLimitVO;
import com.hncboy.chatgpt.admin.service.RateLimitService;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.handler.RateLimiterHandler;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023-4-1
 * 限流记录业务实现类
 */
@Service
public class RateLimitServiceImpl implements RateLimitService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public List<RateLimitVO> listRateLimit() {
        List<RateLimitVO> rateLimitList = new ArrayList<>();
        Map<String, Deque<LocalDateTime>> requestTimestampMap = RateLimiterHandler.IP_REQUEST_TIMESTAMP_MAP;
        Deque<LocalDateTime> globalRequestTimestampQueue = RateLimiterHandler.GLOBAL_REQUEST_TIMESTAMP_QUEUE;

        LocalDateTime now = LocalDateTime.now();

        // 计算全局限流相关信息
        // 统计全局有效请求次数
        int validGlobalRequestCount = (int) globalRequestTimestampQueue.stream().filter(t -> t.isAfter(now.minusSeconds(chatConfig.getMaxRequestSecond()))).count();
        // 判断全局是否被限流
        boolean isGlobalLimited = validGlobalRequestCount >= chatConfig.getMaxRequest();
        // 计算全局下次可发送请求的时间
        Optional<LocalDateTime> globalNextSendTime = isGlobalLimited ?
                globalRequestTimestampQueue.stream().min(Comparator.naturalOrder()).map(dt -> dt.plusSeconds(chatConfig.getMaxRequestSecond())) :
                Optional.empty();

        // 遍历每个 IP 的请求时间队列
        requestTimestampMap.forEach((ip, deque) -> {
            RateLimitVO rateLimitVO = new RateLimitVO();
            rateLimitVO.setIp(ip);
            // IP 和全局限制规则
            rateLimitVO.setIpLimitRule(chatConfig.getIpMaxRequest() + "/" + chatConfig.getIpMaxRequestSecond() + "s");
            rateLimitVO.setGlobalLimitRule(chatConfig.getMaxRequest() + "/" + chatConfig.getMaxRequestSecond() + "s");

            // 统计 IP 有效请求次数
            int validIpRequestCount = (int) deque.stream().filter(t -> t.isAfter(now.minusSeconds(chatConfig.getIpMaxRequestSecond()))).count();
            rateLimitVO.setAlreadySendCount(validIpRequestCount);

            // 判断 IP 是否被限流
            rateLimitVO.setIsIpLimited(validIpRequestCount >= chatConfig.getIpMaxRequest());
            rateLimitVO.setIsGlobalLimited(isGlobalLimited);

            // 计算 IP 下次可发送请求的时间
            Optional<LocalDateTime> ipNextSendTime = rateLimitVO.getIsIpLimited() ?
                    deque.stream().min(Comparator.naturalOrder()).map(dt -> dt.plusSeconds(chatConfig.getIpMaxRequestSecond())) :
                    Optional.empty();

            // 获取下次可发送请求的时间
            LocalDateTime nextSendTime = ipNextSendTime
                    .flatMap(ipTime -> globalNextSendTime.map(globalTime -> ipTime.isAfter(globalTime) ? ipTime : globalTime))
                    .orElse(ipNextSendTime.orElse(globalNextSendTime.orElse(null)));

            if (Objects.isNull(nextSendTime)) {
                rateLimitVO.setNextSendTime("N/A");
            } else {
                rateLimitVO.setNextSendTime(LocalDateTimeUtil.format(nextSendTime, DatePattern.NORM_DATETIME_PATTERN));
            }

            rateLimitList.add(rateLimitVO);
        });

        return rateLimitList;
    }
}
