package cn.beehive.base.domain.entity;

import cn.beehive.base.enums.MessageTypeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间消息
 */
@Data
@TableName("bh_room_bing_msg")
public class RoomBingMsgDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

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
