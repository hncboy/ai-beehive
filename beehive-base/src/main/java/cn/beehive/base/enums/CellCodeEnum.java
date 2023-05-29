package cn.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 编码枚举
 */
@AllArgsConstructor
public enum CellCodeEnum {

    /**
     * OPENAI_CHAT
     */
    OPENAI_CHAT("openai_chat"),

    /**
     * NEW_BING
     */
    NEW_BING("new_bing"),

    /**
     * Midjourney
     */
    MIDJOURNEY("Midjourney");

    @Getter
    @JsonValue
    @EnumValue
    private final String code;

    /**
     * code 作为 key，封装为 Map
     */
    public static final Map<String, CellCodeEnum> CODE_MAP = Stream
            .of(CellCodeEnum.values())
            .collect(Collectors.toMap(CellCodeEnum::getCode, Function.identity()));

    /**
     * 静态工厂反序列化
     *
     * @param code code
     * @return CellCodeEnum
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CellCodeEnum valueOfKey(String code) {
        // 忽略大小写
        return Optional.ofNullable(CODE_MAP.get(code.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException(code));
    }
}
