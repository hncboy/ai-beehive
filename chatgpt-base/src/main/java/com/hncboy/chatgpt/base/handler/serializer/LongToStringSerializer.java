package com.hncboy.chatgpt.base.handler.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author hncboy
 * @date 2023-4-1
 * Long 转 String
 */
public class LongToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(String.valueOf(value));
    }
}
