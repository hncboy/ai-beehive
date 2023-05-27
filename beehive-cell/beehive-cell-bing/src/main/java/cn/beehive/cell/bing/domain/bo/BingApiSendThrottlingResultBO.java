package cn.beehive.cell.bing.domain.bo;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/26
 * BingApi 节流响应参数
 */
@Data
public class BingApiSendThrottlingResultBO {

    /**
     * 用户最大提问次数
     */
    private Integer maxNumUserMessagesInConversation;

    /**
     * 用户当前提问次数
     */
    private Integer numUserMessagesInConversation;
}
