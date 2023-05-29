package cn.beehive.cell.bing.handler;

import cn.beehive.base.exception.ServiceException;
import cn.beehive.cell.base.hander.strategy.DataWrapper;
import cn.beehive.cell.base.hander.strategy.ICellConfigCodeEnum;
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
        public void firstValidate(DataWrapper dataWrapper) {
            /*
             * h3precise     -- 准确模式
             * h3imaginative -- 创造模式
             * harmonyv3     -- 均衡模式
             */
            String mode = dataWrapper.asString();
            if (StrUtil.containsAny(mode, "h3precise", "h3imaginative", "harmonyv3")) {
                return;
            }
            throw new ServiceException(StrUtil.format("NewBing 模式 {} 参数错误", mode));
        }
    }
}
