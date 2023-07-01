package com.hncboy.beehive.cell.openai.enums;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.http.HttpUtil;
import com.unfbx.chatgpt.entity.images.SizeEnum;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像配置项枚举
 */
public enum OpenAiImageCellConfigCodeEnum implements ICellConfigCodeEnum {

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
     * 尺寸
     */
    SIZE {
        @Override
        public String getCode() {
            return "size";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("尺寸不能为空");
            String string = dataWrapper.asString();

            // 判断尺寸是否在枚举内
            for (SizeEnum sizeEnum : SizeEnum.values()) {
                if (sizeEnum.getName().equals(string)) {
                    return;
                }
            }

            throw new ServiceException("尺寸不合法");
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
