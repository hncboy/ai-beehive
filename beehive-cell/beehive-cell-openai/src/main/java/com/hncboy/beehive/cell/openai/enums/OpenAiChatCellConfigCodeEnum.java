package com.hncboy.beehive.cell.openai.enums;

import cn.hutool.core.util.NumberUtil;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.http.HttpUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话 Cell 配置项枚举
 */
public enum OpenAiChatCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 模型
     */
    MODEL {
        @Override
        public String getCode() {
            return "model";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (!OpenAiChatApiModelEnum.NAME_MAP.containsKey(dataWrapper.asString())) {
                throw new ServiceException("模型参数错误");
            }
        }
    },

    /**
     * 单次回复限制
     */
    MAX_TOKENS {
        @Override
        public String getCode() {
            return "max_tokens";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }
            if (!NumberUtil.isInteger(dataWrapper.asString())) {
                throw new ServiceException("maxTokens 限制范围是 [100, 10000]");
            }
            int maxTokens = dataWrapper.asInt();
            if (maxTokens < 100 || maxTokens > 10000) {
                throw new ServiceException("maxTokens 限制范围是 [100, 10000]");
            }
        }
    },

    /**
     * 温度，值越大回复越随机
     */
    TEMPERATURE {
        @Override
        public String getCode() {
            return "temperature";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("OpenAi 温度不能为空");

            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("2")) > 0) {
                throw new ServiceException("温度范围是 [0, 2]");
            }
        }
    },

    /**
     * 话题新鲜度，值越大越有可能扩展到新话题
     */
    PRESENCE_PENALTY {
        @Override
        public String getCode() {
            return "presence_penalty";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("话题新鲜度不能为空");

            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(new BigDecimal("-2")) < 0 || value.compareTo(new BigDecimal("2")) > 0) {
                throw new ServiceException("话题新鲜度范围是 [-2, 2]");
            }
        }
    },

    /**
     * 系统预设消息
     */
    SYSTEM_MESSAGE {
        @Override
        public String getCode() {
            return "system_message";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }
            int length = dataWrapper.asString().length();
            if (length > 2000) {
                throw new ServiceException("系统预设消息长度不能超过 2000");
            }
        }
    },

    /**
     * ApiKey
     */
    API_KEY {
        @Override
        public String getCode() {
            return "api_key";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("ApiKey 不能为空");
        }
    },

    /**
     * OpenAi 请求地址
     */
    OPENAI_BASE_URL {
        @Override
        public String getCode() {
            return "openai_base_url";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("代理地址不能为空");
            String baseUrl = dataWrapper.asString();
            if (!HttpUtil.isHttp(baseUrl) && !HttpUtil.isHttps(baseUrl)) {
                throw new ServiceException("代理地址格式错误");
            }
        }

        @Override
        public void compositeValidateSelf(Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamMap, CellCodeEnum cellCode) {
            // 防止系统 apiKey 被其他代理泄露
            if (roomConfigParamMap.get(API_KEY).getIsUseDefaultValue()) {
                throw new ServiceException("使用自己的代理地址无法使用系统默认的 apiKey，请填写自己的 apiKey");
            }
        }
    },

    /**
     * 请求携带的上下文条数
     * 0 表示不关联上下文
     */
    CONTEXT_COUNT {
        @Override
        public String getCode() {
            return "context_count";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            int count = dataWrapper.asInt();
            if (count < 0 || count > 64) {
                throw new ServiceException("上下文条数范围为 [0, 64]");
            }
        }
    },

    /**
     * 上下文关联的时间范围（小时）
     * 0 表示不关联
     */
    CONTEXT_RELATED_TIME_HOUR {
        @Override
        public String getCode() {
            return "context_related_time_hour";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }
            int hour = dataWrapper.asInt();
            if (hour < 0 || hour > 72) {
                throw new ServiceException("上下文关联时间范围小时为 [0, 72]");
            }
        }
    },

    /**
     * 是否启用本地敏感词
     */
    ENABLED_LOCAL_SENSITIVE_WORD {
        @Override
        public String getCode() {
            return "enabled_local_sensitive_word";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            // 校验是否是 boolean 类型
            dataWrapper.asBoolean();
        }
    },

    /**
     * 是否启用百度内容审核
     */
    ENABLED_BAIDU_AIP {
        @Override
        public String getCode() {
            return "enabled_baidu_aip";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            // 校验是否是 boolean 类型
            dataWrapper.asBoolean();
        }
    },

    /**
     * KEY 使用策略
     */
    KEY_STRATEGY {

        @Override
        public String getCode() {
            return "key_strategy";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (!OpenAiApiKeyStrategyEnum.CODE_MAP.containsKey(dataWrapper.asString())) {
                throw new ServiceException("Key 策略参数错误");
            }
        }
    }
}
