package com.hncboy.chatgpt.admin.handler.converter;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.admin.domain.vo.SensitiveWordVO;
import com.hncboy.chatgpt.base.domain.entity.SensitiveWordDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/28 23:11
 * 敏感词相关转换
 */
@Mapper
public interface SensitiveWordConverter {

    /**
     * 管理端是否脱敏
     */
    String ADMIN_SENSITIVE_WORD_DESENSITIZED_ENABLED = "chat.admin_sensitive_word_desensitized_enabled";

    SensitiveWordConverter INSTANCE = Mappers.getMapper(SensitiveWordConverter.class);

    List<SensitiveWordVO> entityToVO(List<SensitiveWordDO> sensitiveWordDOList);

    @AfterMapping
    default void afterEntityToVO(SensitiveWordDO sensitiveWordDO, @MappingTarget SensitiveWordVO sensitiveWordVO) {
        boolean desensitizedEnabled = BooleanUtil.toBoolean(SpringUtil.getBean(Environment.class).getProperty(ADMIN_SENSITIVE_WORD_DESENSITIZED_ENABLED));
        if (!desensitizedEnabled) {
            return;
        }
        String word = sensitiveWordDO.getWord();
        // 敏感词脱敏
        if (StrUtil.length(word) >= 4) {
            sensitiveWordVO.setWord(StrUtil.hide(word, 1, word.length() - 1));
        } else if (StrUtil.length(word) >= 2) {
            sensitiveWordVO.setWord(StrUtil.hide(word, 1, word.length()));
        } else {
            sensitiveWordVO.setWord("*");
        }
    }
}
