package com.hncboy.beehive.cell.openai.enums;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;

import java.util.Map;

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
        public void singleValidate(DataWrapper dataWrapper) {
            if (OpenAiChatWebModeEnum.NAME_MAP.containsKey(dataWrapper.asString())) {
                return;
            }
            throw new ServiceException("模型参数错误");
        }

        @Override
        public void compositeValidate(Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamMap, CellCodeEnum cellCode) {
            String defaultValue = roomConfigParamMap.get(OpenAiChatWebCellConfigCodeEnum.MODEL).getDefaultValue();

            // GPT 3.5 模型只能在 3.5 图纸中使用
            if (cellCode == CellCodeEnum.OPENAI_CHAT_WEB_3_5 && ObjectUtil.notEqual(OpenAiChatWebModeEnum.DEFAULT_GPT_3_5.getName(), defaultValue)) {
                throw new ServiceException("模型参数错误，该图纸无法切换模型");
            }

            // GPT 4 图纸无法使用 GPT 3.5 模型
            if (cellCode == CellCodeEnum.OPENAI_CHAT_WEB_4) {
                if (ObjectUtil.notEqual(OpenAiChatWebModeEnum.GPT_4.getName(), defaultValue)) {
                    throw new ServiceException("模型参数错误，该图纸必须使用 GPT-4");
                }
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
     * 代理地址
     */
    PROXY_URL {
        @Override
        public String getCode() {
            return "proxy_url";
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
            // 防止系统 accessToken 被其他代理泄露
            if (roomConfigParamMap.get(ACCESS_TOKEN).getIsUseDefaultValue()) {
                throw new ServiceException("使用自己的代理地址无法使用系统默认的 accessToken，请填写自己的 accessToken");
            }
        }
    },
}
