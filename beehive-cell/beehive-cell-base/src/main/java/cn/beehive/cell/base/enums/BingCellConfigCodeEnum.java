package cn.beehive.cell.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/29
 * 填写注释
 */
@AllArgsConstructor
public enum BingCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 模式
     */
    MODE("mode");

    @Getter
    private final String code;
}
