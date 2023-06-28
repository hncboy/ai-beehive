package com.hncboy.beehive.base.resource.aip;

import com.hncboy.beehive.base.util.ObjectMapperUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baidu.aip.contentcensor.AipContentCensor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/6/6
 * 百度 AI 处理
 */
@Slf4j
public class BaiduAipHandler {

    /**
     * 文本审核
     *
     * @param identify 标识
     * @param text     文本内容
     * @return 审核是否通过
     */
    public static Pair<Boolean, String> isCheckTextPass(String identify, String text) {
        // 实时查询缓存判断是否启用
        if (!BaiduAipUtil.getBaiduAipProperties().getEnabled()) {
            return new Pair<>(true, null);
        }

        long currentTime = DateUtil.current();
        log.info("审核标识：{}，文本审核，开始审核时间：{}, 审核内容：{}", identify, currentTime, text);
        Map<String, Object> resultMap = SpringUtil.getBean(AipContentCensor.class).textCensorUserDefined(text).toMap();
        log.info("审核标识：{}，文本审核，审核总时长：{} 毫秒，审核结果：{}", identify, DateUtil.spendMs(currentTime), resultMap);

        if (handleReturnResult(resultMap)) {
            return new Pair<>(true, null);
        }

        return new Pair<>(false, resultMap.toString());
    }

    /**
     * 处理返回结果
     *
     * @param checkTestResult 结果
     */
    private static boolean handleReturnResult(Map<String, Object> checkTestResult) {
        AipContentResult aipContentResult = ObjectMapperUtil.fromJson(ObjectMapperUtil.toJson(checkTestResult), AipContentResult.class);
        // 系统错误
        if (StrUtil.isNotBlank(aipContentResult.getErrorMsg())) {
            return false;
        }

        // 是否合规
        return Objects.equals(aipContentResult.getConclusionType(), 1);
    }
}
