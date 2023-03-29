package com.hncboy.chatgpt.base.handler.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.hncboy.chatgpt.base.annotation.IpLimit;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.base.util.WebUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;


/**
 * @author lizhongyuan
 */
@Aspect
@Component
public class IpLimitAspect {

    private final Map<String, List<Instant>> ipCountMap = MapUtil.newConcurrentHashMap();

    @Pointcut("@annotation(com.hncboy.chatgpt.base.annotation.IpLimit)")
    public void ipLimit() {

    }

    @Around(value = "ipLimit()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        IpLimit ipLimit = signature.getMethod().getAnnotation(IpLimit.class);
        String ip = WebUtil.getIp();
        List<Instant> ipCount = ipCountMap.getOrDefault(ip, CollUtil.newArrayList());
        Instant expiredTime = Instant.now().minus(Duration.ofSeconds(ipLimit.expire()));
        CollUtil.removeWithAddIf(ipCount, value -> value.isBefore(expiredTime));
        int requestCount = ipCount.size();
        if (requestCount >= ipLimit.count()) {
            throw new ServiceException("当前 ip 访问过多，请等待");
        }
        ipCount.add(Instant.now());
        return joinPoint.proceed();
    }

}