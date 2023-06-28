package com.hncboy.beehive.cell.bing.enums;

import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.hutool.core.util.StrUtil;

/**
 * @author hncboy
 * @date 2023/5/29
 * NewBing Cell 配置项枚举
 */
public enum BingCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 模式
     */
    MODE {
        @Override
        public String getCode() {
            return "mode";
        }

        @Override
        public void singleValidate(DataWrapper dataWrapper) {
            String mode = dataWrapper.asString();
            if (BingModeEnum.NAME_MAP.containsKey(mode)) {
                return;
            }
            throw new ServiceException(StrUtil.format("NewBing 模式 {} 参数错误", mode));
        }
    }

    // TODO 看下有无 cookie 有什么区别，如果需要的话，可以加个 cookie 配置项
}
