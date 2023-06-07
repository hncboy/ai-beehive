package cn.beehive.cell.openai.enums;

import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.ThrowExceptionUtil;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import com.unfbx.chatgpt.entity.images.SizeEnum;

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

            // TODO 判断是否合法
            // TODO 使用自己的 open baseUrl 无法使用系统默认的 apiKey，防止 apiKey 被泄露
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
            ThrowExceptionUtil.isTrue(dataWrapper.isNull()).throwMessage("open_base_url 不能为空");
            // TODO 判断是否合法
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
    }
}
