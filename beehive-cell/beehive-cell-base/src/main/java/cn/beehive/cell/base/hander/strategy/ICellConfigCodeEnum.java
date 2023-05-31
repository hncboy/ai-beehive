package cn.beehive.cell.base.hander.strategy;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置参数枚举接口
 */
public interface ICellConfigCodeEnum {

    /**
     * 获取配置参数 code
     *
     * @return 配置参数 code
     */
    String getCode();

    /**
     * 校验配置参数值
     *
     * @param dataWrapper 配置参数值
     */
    default void firstValidate(DataWrapper dataWrapper) {

    }
}
