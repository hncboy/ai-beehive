package com.hncboy.beehive.base.handler.serializer;

import com.hncboy.beehive.base.util.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author hncboy
 * @date 2023/6/26
 * 文件路径前缀序列化
 */
public class FilePathPrefixSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StrUtil.isBlank(s)) {
            return;
        }
        jsonGenerator.writeString(FileUtil.getFilePathVisitPrefix().concat(s));
    }
}
