package cn.beehive.base.handler.cellconfig;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.MD5;

/**
 * Cell配置项需要实现的接口
 *
 * @author CoDeleven
 */
public interface ICellConfigItem {

    /**
     * 配置项名称，相同cell唯一
     *
     * @return 配置项名称
     */
    String getConfigKeyName();

    /**
     * 该配置项是否要持久化到数据库中
     *
     * @return true, 可以; false, 不可以
     */
    boolean isPersistence();

    /**
     * 该字段是否必填
     *
     * @return true, 可以; false, 不可以
     */
    boolean isRequired();

    /**
     * 用户是否可见
     *
     * @return true, 可以; false, 不可以
     */
    boolean isUserVisible();

    /**
     * 用户是否可以修改这个配置项
     *
     * @return true, 可以; false, 不可以
     */
    boolean isUserModifiable();

    /**
     * 用户是否可以实时修改该配置项（在创建出Cell Instance（Room）之后）
     *
     * @return true, 可以; false, 不可以
     */
    boolean isUserLiveModifiable();

    /**
     * 获取该配置项的示例值，该示例值仅用于前端展示
     *
     * @return 返回该配置项的示例值。不存在返回 null 即可
     */
    String getExampleValue();

    /**
     * 获取该配置项的默认值
     *
     * @return 返回该配置项的默认值
     */
    String getDefaultValue();

    String getRemark();

    String getIntroduce();

    Integer getVersion();

    /**
     * 获取当前配置项的MD5，用于和数据库比较，判断是否发生更新
     *
     * @return 当前配置项的属性MD5
     */
    default String getMD5() {
        // 不采用Class获取枚举对象来处理，这是因为无法保证不同VM下都是相同的字段顺序
        String sb = String.valueOf(this.isRequired()) +
                this.isUserVisible() +
                this.isUserModifiable() +
                this.isUserLiveModifiable() +
                this.getExampleValue() +
                this.getIntroduce() +
                this.getRemark() +
                this.getDefaultValue() +
                this.getConfigKeyName();
        return MD5.create().digestHex(sb);
    }
}
