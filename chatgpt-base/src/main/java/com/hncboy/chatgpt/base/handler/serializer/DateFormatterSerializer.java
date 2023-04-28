package com.hncboy.chatgpt.base.handler.serializer;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * @author hncboy
 * @date 2023-4-1
 * 日期格式化序列化
 */
public class DateFormatterSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DateUtil.formatDateTime(date));
    }
}
