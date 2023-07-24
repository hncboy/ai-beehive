package com.hncboy.beehive.cell.wxqf.enums;

import cn.hutool.core.util.NumberUtil;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

import java.math.BigDecimal;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 ERNIE-Bot Cell 配置项枚举
 */
public enum WxqfChatErnieBotCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 温度说明：
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认 0.95，范围 (0, 1.0]，不能为 0
     * （3）建议该参数和 top_p 只设置1个
     * （4）建议 top_p 和 temperature 不要同时更改
     */
    TEMPERATURE {
        @Override
        public String getCode() {
            return "temperature";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull() || !NumberUtil.isDouble(dataWrapper.asString())) {
                throw new ServiceException("temperature 限制范围是 (0, 1.0]，不能为 0，保留一位小数");
            }
            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(BigDecimal.ONE) > 0) {
                throw new ServiceException("temperature 限制范围是 (0, 1.0]，不能为 0，保留一位小数");
            }
        }
    },

    /**
     * 说明：
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认 0.95，范围 (0, 1.0]，不能为 0
     * （3）建议该参数和 top_p 只设置1个
     * （4）建议 top_p 和 temperature 不要同时更改
     */
    TOP_P {
        @Override
        public String getCode() {
            return "top_p";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull() || !NumberUtil.isDouble(dataWrapper.asString())) {
                throw new ServiceException("top_p 限制范围是 (0, 1.0]，不能为 0，保留一位小数");
            }
            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(BigDecimal.ONE) > 0) {
                throw new ServiceException("top_p 限制范围是 (0, 1.0]，不能为 0，保留一位小数");
            }
        }
    },

    /**
     * 通过对已生成的 token 增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    PENALTY_SCORE {
        @Override
        public String getCode() {
            return "penalty_score";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            if (dataWrapper.isNull() || !NumberUtil.isDouble(dataWrapper.asString())) {
                throw new ServiceException("penalty_score 限制范围是 [1.0, 2.0]，保留一位小数");
            }
            BigDecimal value = dataWrapper.asBigDecimal();
            if (value.compareTo(new BigDecimal("1.0")) < 0 || value.compareTo(new BigDecimal("2.0")) > 0) {
                throw new ServiceException("penalty_score 限制范围是 [1.0, 2.0]，保留一位小数");
            }
        }
    },

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    USER_ID {
        @Override
        public String getCode() {
            return "user_id";
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
            if (count < 0 || count > 32) {
                throw new ServiceException("上下文条数范围为 [0, 32]");
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
     * AccessToken
     */
    ACCESS_TOKEN {

        @Override
        public String getCode() {
            return "access_token";
        }
    },

    /**
     * 请求地址
     */
    REQUEST_URL {

        @Override
        public String getCode() {
            return "request_url";
        }
    }
}
