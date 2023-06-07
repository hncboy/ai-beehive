package cn.beehive.cell.openai.enums;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.ThrowExceptionUtil;
import cn.beehive.cell.core.domain.bo.RoomConfigParamBO;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.core.util.ObjectUtil;

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

        @Override
        public void compositeValidate(Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamMap, CellCodeEnum cellCode) {
            // 如果填了自己的模型，就不能用系统的默认 apiKey，因为只有两种模型，apiKey 都是对应的，也防止盗刷其他模型

            String modelName = roomConfigParamMap.get(MODEL).getValue();
            Boolean isUseDefaultValue = roomConfigParamMap.get(API_KEY).getIsUseDefaultValue();

            // 如果当前是 GPT-3.5 图纸但是用的是 GPT-3.5 Turbo 模型，就必须用自己的 apiKey
            if (cellCode == CellCodeEnum.OPENAI_CHAT_API_3_5 && ObjectUtil.notEqual(OpenAiChatApiModelEnum.GPT_3_5_TURBO.getName(), modelName)) {
                ThrowExceptionUtil.isTrue(isUseDefaultValue).throwMessage("切换模型只支持使用自己的 apiKey");
            }

            // GPT-4 同理
            if (cellCode == CellCodeEnum.OPENAI_CHAT_API_4 && ObjectUtil.notEqual(OpenAiChatApiModelEnum.GPT_4.getName(), modelName)) {
                ThrowExceptionUtil.isTrue(isUseDefaultValue).throwMessage("切换模型只支持使用自己的 apiKey");
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
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("代理地址不能为空");
            // TODO 判断是否合法
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
            if (hour < 1 || hour > 72) {
                throw new ServiceException("上下文关联时间范围小时为 [1, 72]");
            }
        }
    },

    /**
     * 是否联网
     */
    /*ENABLE_NETWORKING {

        @Override
        public String getCode() {
            return "enable_networking";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull()) {
                throw new ServiceException("是否联网不能为空");
            }
            // 校验是否是 boolean 类型
            dataWrapper.asBoolean();
        }
    }*/
}
