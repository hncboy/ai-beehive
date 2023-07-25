package com.hncboy.beehive.cell.midjourney.domain.bo;

import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/20
 * Midjourney Discord 消息业务对象
 */
@Data
public class MidjourneyDiscordMessageBO {

    /**
     * 消息指令动作
     */
    private MidjourneyMsgActionEnum action;

    /**
     * 消息内容
     */
    private String prompt;

    /**
     * 位置
     */
    private Integer index;

    /**
     * 消息状态
     */
    private String status;
}
