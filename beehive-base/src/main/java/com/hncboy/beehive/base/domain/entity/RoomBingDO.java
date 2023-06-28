package com.hncboy.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间表实体类
 */
@Data
@TableName("bh_room_bing")
public class RoomBingDO {

    /**
     * 房间 id
     */
    @TableId
    private Long roomId;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * bing 模式
     */
    private String mode;

    /**
     * bing conversationId
     */
    private String conversationId;

    /**
     * bing clientId
     */
    private String clientId;

    /**
     * bing conversationSignature
     */
    private String conversationSignature;

    /**
     * bing 最大提问次数
     */
    private Integer maxNumUserMessagesInConversation;

    /**
     * bing 累计提问次数
     */
    private Integer numUserMessagesInConversation;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
