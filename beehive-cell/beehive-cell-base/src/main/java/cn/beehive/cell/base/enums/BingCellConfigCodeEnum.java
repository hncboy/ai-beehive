package cn.beehive.cell.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/5/29
 * 填写注释
 */
@AllArgsConstructor
public enum BingCellConfigCodeEnum implements ICellConfigCodeEnum<BingCellConfigCodeEnum> {

    /**
     * 模式
     */
    MODE("mode");

    @Getter
    private final String code;

    @Override
    public Map<String, BingCellConfigCodeEnum> getCodeMap() {
        return BingCellConfigCodeEnum.CODE_MAP;
    }

    /**
     * code 作为 key，封装为 Map
     */
    private static final Map<String, BingCellConfigCodeEnum> CODE_MAP = Stream
            .of(BingCellConfigCodeEnum.values())
            .collect(Collectors.toMap(BingCellConfigCodeEnum::getCode, Function.identity()));
}
