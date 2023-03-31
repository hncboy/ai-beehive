package com.hncboy.chatgpt.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.hncboy.chatgpt.admin.domain.request.SysUserLoginRequest;
import com.hncboy.chatgpt.admin.domain.vo.RateLimitVO;
import com.hncboy.chatgpt.admin.service.SysUserService;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.exception.AuthException;
import com.hncboy.chatgpt.base.handler.RateLimiterHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/3/28 12:44
 * 系统用户业务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public void login(SysUserLoginRequest sysUserLoginRequest) {
        if (sysUserLoginRequest.getAccount().equals(chatConfig.getAdminAccount()) && sysUserLoginRequest.getPassword().equals(chatConfig.getAdminPassword())) {
            StpUtil.login(sysUserLoginRequest.getAccount());
            return;
        }
        throw new AuthException("账号或密码错误");
    }

    @Override
    public List<RateLimitVO> listRateLimit() {
        List<RateLimitVO> rateLimitList = new ArrayList<>();
        Map<String, Deque<LocalDateTime>> requestTimestampMap = RateLimiterHandler.IP_REQUEST_TIMESTAMP_MAP;
        for (Map.Entry<String, Deque<LocalDateTime>> entry : requestTimestampMap.entrySet()) {
            String ip = entry.getKey();
            Deque<LocalDateTime> timestampDeque = entry.getValue();

            RateLimitVO rateLimitVO = new RateLimitVO();
            rateLimitVO.setIp(ip);
            rateLimitVO.setLimitRule(String.format("%d 秒 %d 次", chatConfig.getMaxRequestSecond(), chatConfig.getMaxRequest()));
            rateLimitVO.setAlreadySendCount(timestampDeque.size());
            rateLimitVO.setIsLimited(timestampDeque.size() >= chatConfig.getMaxRequest());
            // 计算下次限流放开的时间点
            if (rateLimitVO.getIsLimited()) {
                assert timestampDeque.peekFirst() != null;
                LocalDateTime nextSendTime = timestampDeque.peekFirst().plusSeconds(chatConfig.getMaxRequestSecond());
                rateLimitVO.setNextSendTime(LocalDateTimeUtil.format(nextSendTime, DatePattern.NORM_DATETIME_PATTERN));
            } else {
                rateLimitVO.setNextSendTime("N/A");
            }
            rateLimitList.add(rateLimitVO);
        }

        return rateLimitList;
    }
}
