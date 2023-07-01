package com.hncboy.beehive.cell.openai.handler.scheduler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import com.unfbx.chatgpt.OpenAiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
 */
@Slf4j
@Component
public class OpenAiApiKeyScheduler {

    @Resource
    private OpenAiApiKeyService openAiApiKeyService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void handler() {
        log.info("OpenAi ApiKey 检测定时任务开始");

        // 查询启用的或停用需要刷新额度的
        List<OpenAiApiKeyDO> openAiApiKeyDOList = openAiApiKeyService.list(new LambdaQueryWrapper<OpenAiApiKeyDO>()
                .eq(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.ENABLE)
                .or(wrapper -> wrapper.eq(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.DISABLE)
                        .eq(OpenAiApiKeyDO::getIsRefreshBalance, true)));
        for (OpenAiApiKeyDO openAiApiKeyDO : openAiApiKeyDOList) {
            try {
                // 构建 OpenAiClient
                OpenAiClient openAiClient = OpenAiClient.builder()
                        .apiKey(Collections.singletonList(openAiApiKeyDO.getApiKey()))
                        .okHttpClient(OkHttpClientUtil.getProxyInstance())
                        .build();
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

                OpenAiApiKeyStatusEnum statusEnum = openAiApiKeyDO.getStatus();
                String updateReason;
                if (remainBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    // 小于等于 0 就不用刷新了
                    openAiApiKeyDO.setIsRefreshBalance(false);
                    updateReason = "剩余额度小于等于 0，停用";
                    statusEnum = OpenAiApiKeyStatusEnum.DISABLE;
                } else if (remainBalance.compareTo(openAiApiKeyDO.getBalanceWaterLine()) <= 0) {
                    updateReason = "剩余额度小于水位线，停用";
                    statusEnum = OpenAiApiKeyStatusEnum.DISABLE;
                } else {
                    updateReason = "刷新额度";
                }

                openAiApiKeyDO.setTotalBalance(totalBalance);
                openAiApiKeyDO.setUsageBalance(totalUsage);
                openAiApiKeyDO.setRemainBalance(remainBalance);
                openAiApiKeyDO.setUpdateReason(updateReason);
                openAiApiKeyDO.setRefreshBalanceTime(new Date());
                openAiApiKeyDO.setStatus(statusEnum);
                openAiApiKeyService.updateById(openAiApiKeyDO);
            } catch (Exception e) {
                openAiApiKeyDO.setUpdateReason("刷新额度出现异常，置为失效；异常信息".concat(ExceptionUtil.stacktraceToString(e)));
                openAiApiKeyDO.setRefreshBalanceTime(new Date());
                openAiApiKeyDO.setStatus(OpenAiApiKeyStatusEnum.INVALID);
                openAiApiKeyService.updateById(openAiApiKeyDO);
            }
        }
    }
}
