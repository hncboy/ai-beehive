package com.hncboy.beehive.cell.bing.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * BingApi type=1 参数
 * 情况 1 消息不考虑，情况 2 就是流式消息了
 * 情况 1：{"type":1,"target":"update","arguments":[{"requestId":"949c29a8-d9f0-4828-865f-8133e34f9519","throttling":{"maxNumUserMessagesInConversation":5,"numUserMessagesInConversation":1}}]}
 * 情况 2：{"type":1,"target":"update","arguments":[{"messages":[{"text":"你","author":"bot","createdAt":"2023-05-26T12:38:17.9978221+00:00","timestamp":"2023-05-26T12:38:17.9978221+00:00","messageId":"5d91d7e3-bddd-4dfa-9ee6-d6c54a9ac81c","offense":"Unknown","adaptiveCards":[{"type":"AdaptiveCard","version":"1.0","body":[{"type":"TextBlock","text":"你","wrap":true}]}],"sourceAttributions":[],"feedback":{"tag":null,"updatedOn":null,"type":"None"},"contentOrigin":"DeepLeo","privacy":null}],"requestId":"dce0660c-660e-4068-bd53-da57cb9f441f"}]}*/
@Data
public class BingApiSendType1ResultBO {

    // 省略其他参数

    private Integer type;
    private List<Argument> arguments;

    @Data
    public static class Argument {
        private List<BingApiSendMessageResultBO> messages;
        private BingApiSendThrottlingResultBO throttling;
    }
}
