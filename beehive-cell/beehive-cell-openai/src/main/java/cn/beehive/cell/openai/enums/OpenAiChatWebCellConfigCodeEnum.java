package cn.beehive.cell.openai.enums;

import cn.beehive.base.exception.ServiceException;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话 Web Cell 配置项枚举
 */
public enum OpenAiChatWebCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 模型
     */
    MODEL {
        @Override
        public String getCode() {
            return "model";
        }

        @Override
        public void firstValidate(DataWrapper dataWrapper) {
            if (OpenAiChatWebModeEnum.NAME_MAP.containsKey(dataWrapper.asString())) {
                return;
            }
            throw new ServiceException("OpenAi Web 模型参数错误");
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

        @Override
        public void firstValidate(DataWrapper dataWrapper) {
            // TODO 可以校验 AccessToken 是否合法和有效
        }
    },

    /**
     * 代理地址
     */
    PROXY_URL {
        @Override
        public String getCode() {
            return "proxy_url";
        }

        @Override
        public void firstValidate(DataWrapper dataWrapper) {
            // TODO 简单校验是否满足 HTTP/HTTPS 地址格式
        }
    },
}
