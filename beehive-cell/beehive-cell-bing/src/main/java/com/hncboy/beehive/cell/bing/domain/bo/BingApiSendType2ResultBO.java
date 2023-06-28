package com.hncboy.beehive.cell.bing.domain.bo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * BingApi type=2 参数
 */
@Data
public class BingApiSendType2ResultBO {

    // 省略其他参数

    private Integer type;
    private Item item;

    @Data
    public static class Item {

        private List<BingApiSendMessageResultBO> messages;

        /**
         * 对话过期时间，时区不一样，不能直接判断
         */
        private Date conversationExpiryTime;

        private BingApiSendThrottlingResultBO throttling;

        private Result result;
    }

    @Data
    public static class Result {

        /**
         * Success || InvalidSession || UnauthorizedRequest || ProcessingMessage || InvalidRequest
         * <p>
         * InvalidRequest：有时候网络问题会报这个错，可能下次重试就好了，不过暂不考虑
         * {"type":2,"invocationId":"1","item":{"firstNewMessageIndex":null,"defaultChatName":null,"conversationId":"51D|BingProd|B5BEEE7E8F18CF88AAB6213F3C769E4D626C1EDCAE0EF28A8997749F397B939F","requestId":"a3a5b0ff-23fd-438a-b7b7-b577d860f00f","telemetry":{"metrics":null,"startTime":"2023-05-26T14:13:38.8915367Z"},"result":{"value":"InvalidRequest","message":"Conversation '51D|BingProd|B5BEEE7E8F18CF88AAB6213F3C769E4D626C1EDCAE0EF28A8997749F397B939F' with 8 messages already exists.","error":"InvalidRequest","serviceVersion":"20230525.81"}}}{"type":3,"invocationId":"1"}
         * InvalidSession：{"type":2,"invocationId":"2","item":{"firstNewMessageIndex":null,"defaultChatName":null,"conversationId":"51D|BingProd|8B0DC029BD129C2808B75EA6D1ACBD7DAFA60726C7A07F402C8599642678A45B","requestId":"0aa2772e-4a77-40cd-aad3-056ea3b67f8b","telemetry":{"metrics":null,"startTime":"2023-05-27T09:27:19.5991442Z"},"result":{"value":"InvalidSession","message":"Conversation '51D|BingProd|8B0DC029BD129C2808B75EA6D1ACBD7DAFA60726C7A07F402C8599642678A45B' doesn't exist or has expired. Conversations expire after 06:00:00.","serviceVersion":"20230526.52"}}}{"type":3,"invocationId":"2"}
         */
        private String value;
    }
}
