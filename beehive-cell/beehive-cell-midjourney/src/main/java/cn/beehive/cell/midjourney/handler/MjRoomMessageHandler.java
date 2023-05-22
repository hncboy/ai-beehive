package cn.beehive.cell.midjourney.handler;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.domain.request.MjConvertRequest;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import cn.beehive.cell.midjourney.util.MjRoomMessageUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/22
 * Midjourney 房间消息处理器
 */
public class MjRoomMessageHandler {

    /**
     * 检查是否存在正在处理的任务
     */
    public static void checkExistProcessingTask() {
        RoomMjMsgService roomMjMsgService = SpringUtil.getBean(RoomMjMsgService.class);
        long count = roomMjMsgService.count(new LambdaQueryWrapper<RoomMjMsgDO>()
                .eq(RoomMjMsgDO::getUserId, FrontUserUtil.getUserId())
                .in(RoomMjMsgDO::getType, MessageTypeEnum.ANSWER)
                // 进行中的状态
                .in(RoomMjMsgDO::getStatus, MjMsgStatusEnum.SYS_QUEUING, MjMsgStatusEnum.MJ_WAIT_RECEIVED, MjMsgStatusEnum.MJ_IN_PROGRESS));
        if (count > 0) {
            throw new ServiceException("当前用户存在 Midjourney 正在处理的任务，请等待任务执行完毕后再试");
        }
    }

    /**
     * 检查是否可以进行 upscale
     *
     * @param parentRoomMjMsgDO 父消息
     * @param convertRequest    转换请求
     * @param midjourneyConfig  Midjourney 配置
     */
    public static void checkCanUpscale(RoomMjMsgDO parentRoomMjMsgDO, MjConvertRequest convertRequest, MidjourneyConfig midjourneyConfig) {
        if (Objects.isNull(parentRoomMjMsgDO)) {
            throw new ServiceException("消息不存在");
        }
        // 只有答案消息 且 状态为成功的消息 且 动作为 imagine 的消息才能进行 u 转换
        if (parentRoomMjMsgDO.getType() != MessageTypeEnum.ANSWER
                || parentRoomMjMsgDO.getAction() != MjMsgActionEnum.IMAGINE
                || parentRoomMjMsgDO.getStatus() != MjMsgStatusEnum.MJ_SUCCESS) {
            throw new ServiceException("该消息无法进行 u 转换");
        }
        if (ObjectUtil.notEqual(parentRoomMjMsgDO.getDiscordChannelId(), midjourneyConfig.getChannelId())) {
            throw new ServiceException("由于 Discord 频道切换，该图无法进行转换操作，请重新生成");
        }
        // 判断是否已经 u 转换过
        boolean isUse = MjRoomMessageUtil.isUVUse(parentRoomMjMsgDO.getUvUseBit(), convertRequest.getIndex(), MjMsgActionEnum.UPSCALE);
        if (isUse) {
            throw new ServiceException("该消息已经进行过 U" + convertRequest.getIndex());
        }

        // 查询是否存在转换过的消息
        RoomMjMsgService roomMjMsgService = SpringUtil.getBean(RoomMjMsgService.class);

        // 查所有位置的数据，因为回调不知道是哪个位置的数据，所以同一时间处理的只能有一个操作
        RoomMjMsgDO existProcessingRoomMessage = roomMjMsgService.getOne(new LambdaQueryWrapper<RoomMjMsgDO>().eq(RoomMjMsgDO::getUvParentId, parentRoomMjMsgDO.getId())
                .in(RoomMjMsgDO::getStatus, MjMsgStatusEnum.SYS_QUEUING, MjMsgStatusEnum.MJ_WAIT_RECEIVED, MjMsgStatusEnum.MJ_IN_PROGRESS)
                // 回答
                .eq(RoomMjMsgDO::getType, MessageTypeEnum.ANSWER)
                // u 转换
                .eq(RoomMjMsgDO::getAction, MjMsgActionEnum.UPSCALE));
        if (Objects.nonNull(existProcessingRoomMessage)) {
            MjMsgStatusEnum convertStatus = existProcessingRoomMessage.getStatus();
            if (convertStatus == MjMsgStatusEnum.SYS_QUEUING) {
                throw new ServiceException(StrUtil.format("存在 U{} 转换正在排队中，请勿重复操作", existProcessingRoomMessage.getUvIndex()));
            }
            if (convertStatus == MjMsgStatusEnum.MJ_WAIT_RECEIVED || convertStatus == MjMsgStatusEnum.MJ_IN_PROGRESS) {
                throw new ServiceException(StrUtil.format("存在 U{} 转换正在处理中，请勿重复操作", existProcessingRoomMessage.getUvIndex()));
            }
        }

        // 理论上这些状态的只会存在一条记录，不考虑并发插入的情况
        RoomMjMsgDO convertRoomMjMsgDO = roomMjMsgService.getOne(new LambdaQueryWrapper<RoomMjMsgDO>().eq(RoomMjMsgDO::getUvParentId, parentRoomMjMsgDO.getId())
                .in(RoomMjMsgDO::getStatus, MjMsgStatusEnum.SYS_SUCCESS, MjMsgStatusEnum.MJ_SUCCESS, MjMsgStatusEnum.MJ_FAILURE,
                        MjMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE, MjMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE)
                // 指定位置
                .eq(RoomMjMsgDO::getUvIndex, convertRequest.getIndex())
                // 回答
                .eq(RoomMjMsgDO::getType, MessageTypeEnum.ANSWER)
                // u 转换
                .eq(RoomMjMsgDO::getAction, MjMsgActionEnum.UPSCALE));
        if (Objects.isNull(convertRoomMjMsgDO)) {
            return;
        }
        MjMsgStatusEnum convertStatus = convertRoomMjMsgDO.getStatus();
        if (convertStatus == MjMsgStatusEnum.SYS_SUCCESS || convertStatus == MjMsgStatusEnum.MJ_SUCCESS) {
            // 理论上不会进入
            throw new ServiceException("该转换已经成功，请勿重复操作");
        }
        if (convertStatus == MjMsgStatusEnum.MJ_FAILURE || convertStatus == MjMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE || convertStatus == MjMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE) {
            throw new ServiceException("该转换曾经失败过，请勿重复操作");
        }
    }

    /**
     * 获取 Discord 消息的 message hash
     *
     * @param discordImageUrl Discord 消息图片地址
     * @return message hash
     */
    public static String getDiscordMessageHash(String discordImageUrl) {
        return CharSequenceUtil.subBefore(discordImageUrl.substring(discordImageUrl.lastIndexOf("_") + 1), ".", true);
    }
}
