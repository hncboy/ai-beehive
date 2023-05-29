package cn.beehive.cell.base.enums;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置参数枚举接口
 */
public interface ICellConfigCodeEnum<T extends ICellConfigCodeEnum<T>> {

    /**
     * 获取配置参数 code
     *
     * @return 配置参数 code
     */
    String getCode();

    /**
     * 获取配置参数 code 作为 key 的 Map
     *
     * @return 配置参数 code 作为 key 的 Map
     */
    Map<String, T> getCodeMap();
}
