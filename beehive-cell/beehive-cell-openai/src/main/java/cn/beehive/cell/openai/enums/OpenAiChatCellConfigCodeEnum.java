package cn.beehive.cell.openai.enums;

import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.ThrowExceptionUtil;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

import java.math.BigDecimal;

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
            return "mode";
        }

        @Override
        public void firstValidate(DataWrapper dataWrapper) {
            String mode = dataWrapper.asString();
            // TODO
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
        public void firstValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }

            int maxTokens = dataWrapper.asInt();
            if (maxTokens < 100 || maxTokens > 10000) {
                throw new ServiceException("OpenAi maxTokens 限制范围是 [100, 10000]");
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
        public void firstValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("OpenAi 温度不能为空");

            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("2")) > 0) {
                throw new ServiceException("OpenAi 温度范围是 [0, 2]");
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
        public void firstValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("OpenAi 话题新鲜度不能为空");

            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(new BigDecimal("-2")) < 0 || value.compareTo(new BigDecimal("2")) > 0) {
                throw new ServiceException("OpenAi 话题新鲜度范围是 [-2, 2]");
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
        public void firstValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }
            int length = dataWrapper.asString().length();
            if (length > 1000) {
                throw new ServiceException("OpenAi 系统预设消息长度不能超过 1000");
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
        public void firstValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("ApiKey 不能为空");

            // TODO 判断是否合法
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
        public void firstValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("open_base_url 不能为空");
            // TODO 判断是否合法
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
        public void firstValidate(DataWrapper dataWrapper) {
            int count = dataWrapper.asInt();
            if (count < 0 || count > 64) {
                throw new ServiceException("OpenAi 上下文条数范围为 [0, 64]");
            }
        }
    },

    /**
     * 上下文关联的时间范围（小时）
     * null 表示不关联
     */
    CONTEXT_RELATED_TIME_HOUR {
        @Override
        public String getCode() {
            return "context_related_time_hour";
        }

        @Override
        public void firstValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                return;
            }
            int hour = dataWrapper.asInt();
            if (hour < 1 || hour > 72) {
                throw new ServiceException("OpenAi 上下文关联时间范围小时为 [1, 72]");
            }
        }
    },
}
