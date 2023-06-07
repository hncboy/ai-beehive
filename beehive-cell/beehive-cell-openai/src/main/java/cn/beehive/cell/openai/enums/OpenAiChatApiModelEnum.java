package cn.beehive.cell.openai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/6/7
 * OpenAI Chat API 模型
 * @see <a href="https://platform.openai.com/docs/models/gpt-3-5"/>
 */
@AllArgsConstructor
public enum OpenAiChatApiModelEnum {

    //... 其他模型就不加了

    /**
     * GPT-3.5
     */
    GPT_3_5_TURBO("gpt-3.5-turbo", 4096),

    /**
     * GPT-4
     */
    GPT_4("gpt-4", 8192);

    /**
     * 模型名称
     */
    @Getter
    private final String name;

    /**
     * 最大 token 上限
     */
    @Getter
    private final Integer maxTokens;

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, OpenAiChatApiModelEnum> NAME_MAP = Stream
            .of(OpenAiChatApiModelEnum.values())
            .collect(Collectors.toMap(OpenAiChatApiModelEnum::getName, Function.identity()));

    /**
     * 根据模型名称获取最大 token 上限
     *
     * @param modelName 模型名称
     * @return 最大 token 上限
     */
    public static Integer maxTokens(String modelName) {
        return NAME_MAP.get(modelName).getMaxTokens();
    }
}
