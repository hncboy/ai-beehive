package cn.beehive.base.resource.aip;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/6/6
 * 百度 AI 处理
 */
@Slf4j
@Component
public class BaiduAipHandler {

    @Resource
    private BaiduAipConfig baiduAipConfig;

    /**
     * 文本审核
     *
     * @param identify 标识
     * @param text     文本内容
     * @return 审核是否通过
     */
    public Pair<Boolean, String> isCheckTextPass(String identify, String text) {
        // 实时查询缓存判断是否启用
        String enabledStr = SysParamCache.get(BaiduAipConfig.BaiduAipConstant.ENABLED);
        if (!BooleanUtil.toBoolean(enabledStr)) {
            return new Pair<>(true, null);
        }

        long currentTime = DateUtil.current();
        log.info("审核标识：{}，文本审核，开始审核时间：{}, 审核内容：{}", identify, currentTime, text);
        Map<String, Object> resultMap = baiduAipConfig.getAipContentCensor().textCensorUserDefined(text).toMap();
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
    private boolean handleReturnResult(Map<String, Object> checkTestResult) {
        AipContentResult aipContentResult = ObjectMapperUtil.fromJson(ObjectMapperUtil.toJson(checkTestResult), AipContentResult.class);
        // 系统错误
        if (StrUtil.isNotBlank(aipContentResult.getErrorMsg())) {
            return false;
        }

        // 是否合规
        return Objects.equals(aipContentResult.getConclusionType(), 1);
    }
}
