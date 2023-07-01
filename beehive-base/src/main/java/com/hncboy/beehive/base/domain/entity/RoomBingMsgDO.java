package com.hncboy.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间消息表实体类
 */
@Data
@TableName(value = "bh_room_bing_msg", autoResultMap = true)
public class RoomBingMsgDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父消息 id
     */
    private Long parentMessageId;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * ip
     */
    private String ip;

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

    /**
     * 消息内容
     */
    private String content;

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
     * 是否新话题
     */
    private Boolean isNewTopic;

    /**
     * bing 刷新房间原因
     */
    private String refreshRoomReason;

    /**
     * bing 推荐提问
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> suggestResponses;

    /**
     * bing 数据来源
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Object> sourceAttributions;

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
