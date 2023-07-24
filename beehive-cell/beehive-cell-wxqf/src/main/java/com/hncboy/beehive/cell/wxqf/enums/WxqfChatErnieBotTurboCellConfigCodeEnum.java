package com.hncboy.beehive.cell.wxqf.enums;

import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 ERNIE-Bot-turbo 配置项枚举
 */
public enum WxqfChatErnieBotTurboCellConfigCodeEnum implements ICellConfigCodeEnum {

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
