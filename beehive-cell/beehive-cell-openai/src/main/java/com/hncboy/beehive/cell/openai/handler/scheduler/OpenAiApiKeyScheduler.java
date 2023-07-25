package com.hncboy.beehive.cell.openai.handler.scheduler;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import com.unfbx.chatgpt.OpenAiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.HttpException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 定时任务
 * 该任务目前废弃，因为该接口已经失效
 */
@Deprecated
@Slf4j
@Component
public class OpenAiApiKeyScheduler {

    @Resource
    private OpenAiApiKeyService openAiApiKeyService;

//    @Scheduled(cron = "0 0/30 * * * ?")
    public void handler() {
        log.info("OpenAi ApiKey 检测定时任务开始");

        List<OpenAiApiKeyDO> openAiApiKeyDOList = openAiApiKeyService.list(new LambdaQueryWrapper<OpenAiApiKeyDO>()
                // 查询状态为启用或停用
                .in(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.ENABLE, OpenAiApiKeyStatusEnum.DISABLE)
                // 查询需要刷新状态或者刷新余额的
                .and(wrapper -> wrapper.eq(OpenAiApiKeyDO::getIsRefreshBalance, true)
                        .or(orWrapper -> orWrapper.eq(OpenAiApiKeyDO::getIsRefreshStatus, true))));
        for (OpenAiApiKeyDO openAiApiKeyDO : openAiApiKeyDOList) {
            try {
                // 构建 OpenAiClient
                OpenAiClient openAiClient = OpenAiClient.builder()
                        .apiKey(Collections.singletonList(openAiApiKeyDO.getApiKey()))
                        .okHttpClient(OkHttpClientUtil.getProxyInstance())
                        .build();

                // 是否需要刷新余额，因为余额刷新不一定准，所以可以配置某些 apiKey 不刷新余额
                if (openAiApiKeyDO.getIsRefreshBalance()) {
                    // 总额度
                    BigDecimal totalBalance = BigDecimal.valueOf(openAiClient.subscription().getHardLimitUsd());
                    // 结束日期，包含今天
                    LocalDate endLocalDate = LocalDate.now().plusDays(1);
                    // 开始日期，最多 100 天
                    LocalDate startLocalDate = endLocalDate.minusDays(100);
                    // 已使用额度，返回的是美分，转美元
                    BigDecimal totalUsage = openAiClient.billingUsage(startLocalDate, endLocalDate).getTotalUsage().divide(new BigDecimal(100), RoundingMode.HALF_UP);
                    // 剩余额度
                    BigDecimal remainBalance = totalBalance.subtract(totalUsage);

                    if (remainBalance.compareTo(BigDecimal.ZERO) <= 0) {
                        // 小于等于 0 就不用刷新了
                        openAiApiKeyDO.setIsRefreshBalance(false);
                        openAiApiKeyDO.setUpdateReason("剩余额度小于等于 0，停用");
                        openAiApiKeyDO.setStatus(OpenAiApiKeyStatusEnum.DISABLE);
                    } else if (remainBalance.compareTo(openAiApiKeyDO.getBalanceWaterLine()) <= 0) {
                        openAiApiKeyDO.setStatus(OpenAiApiKeyStatusEnum.DISABLE);
                        openAiApiKeyDO.setUpdateReason("剩余额度小于水位线，停用");
                    } else {
                        openAiApiKeyDO.setUpdateReason("刷新额度成功");
                    }

                    openAiApiKeyDO.setTotalBalance(totalBalance);
                    openAiApiKeyDO.setUsageBalance(totalUsage);
                    openAiApiKeyDO.setRemainBalance(remainBalance);
                    openAiApiKeyDO.setRefreshBalanceTime(new Date());
                } else {
                    // 仅仅是为了刷新状态
                    openAiApiKeyDO.setUpdateReason("刷新成功但是不更新额度");
                }

                openAiApiKeyDO.setRefreshStatusTime(new Date());
                openAiApiKeyDO.setErrorInfo(StrUtil.EMPTY);
                openAiApiKeyService.updateById(openAiApiKeyDO);
            } catch (Exception e) {
                openAiApiKeyDO.setRefreshStatusTime(new Date());
                openAiApiKeyDO.setErrorInfo(ExceptionUtil.stacktraceToString(e));

                if (e instanceof HttpException && ((HttpException) e).code() == 401) {
                    log.warn("OpenAi ApiKey 刷新额度出现 401，ApiKey 信息：{}", ObjectMapperUtil.toJson(openAiApiKeyDO), e);
                    openAiApiKeyDO.setUpdateReason("刷新额度出现 401，疑似 ApkKey 被封禁，置为失效");
                    openAiApiKeyDO.setIsRefreshBalance(false);
                    openAiApiKeyDO.setIsRefreshStatus(false);
                    openAiApiKeyDO.setStatus(OpenAiApiKeyStatusEnum.INVALID);
                } else {
                    // 一般是网络问题不更新状态，可能有网络波动不稳定，此时状态保持不变
                    log.warn("OpenAi ApiKey 刷新额度出现异常，ApiKey 信息：{}", ObjectMapperUtil.toJson(openAiApiKeyDO), e);
                    openAiApiKeyDO.setUpdateReason("刷新额度出现异常");
                    openAiApiKeyDO.setErrorInfo(ExceptionUtil.stacktraceToString(e));
                }

                openAiApiKeyService.updateById(openAiApiKeyDO);
            }

            // 不频繁请求
            ThreadUtil.sleep(1000);
        }
    }
}
