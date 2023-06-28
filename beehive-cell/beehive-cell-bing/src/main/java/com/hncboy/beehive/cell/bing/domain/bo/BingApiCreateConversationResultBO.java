package com.hncboy.beehive.cell.bing.domain.bo;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing Api 创建对话结果
 * {
 *    conversationId: "51D|BingProd|D67BA9D1053F878DE6493F8F20188A931149EF52732504108294ECC6A9D582C4",
 *    clientId: "985154382934072",
 *    conversationSignature: "RGq5JeRrPzjZw+W26Mv2jkdPSRhg0erNZ4SVkDvfUYg=",
 *    result: {
 *        value: "Success",
 *        message: null
 *    }
 * }
 */
@Data
public class BingApiCreateConversationResultBO {

    private String conversationId;
    private String clientId;
    private String conversationSignature;
    private Result result;

    @Data
    public static class Result {
        private String value;
        private String message;
    }
}
