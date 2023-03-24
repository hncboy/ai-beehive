package com.hncboy.chatgpt.api.parser;

/**
 * @author hncboy
 * @date 2023/3/24 17:43
 * 响应数据解析器接口
 */
public interface ResponseParser<SUCCESS> {

    /**
     * 解析响应成功的原始数据
     *
     * @param originalData 原始数据
     * @return 实体类
     */
    SUCCESS parseSuccess(String originalData);

    /**
     * 解析对话内容
     *
     * @param originalData 原始数据
     * @return 消息内容
     */
    String parseContent(String originalData);
}
