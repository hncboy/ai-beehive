package com.hncboy.beehive.cell.chatglm.enums;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

import java.math.BigDecimal;

/**
 * @author hanpeng
 * @date 2023/8/3
 * 对话 ChatGLM 配置项枚举
 */
public enum ChatGlmCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * ChatGLM 请求地址
     */
    CHATGLM_BASE_URL {
        @Override
        public String getCode() {
            return "chatglm_base_url";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("代理地址不能为空");
            String baseUrl = dataWrapper.asString();
            if (!HttpUtil.isHttp(baseUrl) && !HttpUtil.isHttps(baseUrl)) {
                throw new ServiceException("代理地址格式错误");
            }
        }
    },


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
            if (!ChatGlmApiModelEnum.NAME_MAP.containsKey(dataWrapper.asString())) {
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
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("ChatGLM 温度不能为空");

            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("1")) > 0) {
                throw new ServiceException("温度范围是 [0, 1]");
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
}
