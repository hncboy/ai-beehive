package com.hncboy.beehive.cell.midjourney.handler;

import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import com.hncboy.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import com.hncboy.beehive.cell.midjourney.util.MjRoomMessageUtil;
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
public class MidjourneyRoomMsgHandler {

    /**
     * 检查是否可以操作
     *
     * @param roomId 房间 id
     */
    public static void checkCanOperate(Long roomId) {
        // 检查房间是否存在
        RoomHandler.checkRoomExistAndCellCanUse(roomId, CellCodeEnum.MIDJOURNEY);
        // 检查是否有正在处理的任务
        MidjourneyRoomMsgHandler.checkExistProcessingTask();
    }

    /**
     * 检查是否存在正在处理的任务
     */
    public static void checkExistProcessingTask() {
        RoomMidjourneyMsgService roomMidjourneyMsgService = SpringUtil.getBean(RoomMidjourneyMsgService.class);
        long count = roomMidjourneyMsgService.count(new LambdaQueryWrapper<RoomMidjourneyMsgDO>()
                .eq(RoomMidjourneyMsgDO::getUserId, FrontUserUtil.getUserId())
                // 查询回答
                .in(RoomMidjourneyMsgDO::getType, MessageTypeEnum.ANSWER)
                // 进行中的状态
                .in(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.SYS_QUEUING, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED, MidjourneyMsgStatusEnum.MJ_IN_PROGRESS));
        if (count > 0) {
            throw new ServiceException("当前用户存在 Midjourney 正在处理的任务，请等待任务执行完毕后再试");
        }
    }

    /**
     * 检查是否可以进行 upscale
     *
     * @param parentRoomMidjourneyMsgDO 父消息
     * @param index                     位置
     * @param midjourneyProperties      Midjourney 配置
     */
    public static void checkCanUpscale(RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO, int index, MidjourneyProperties midjourneyProperties) {
        // 检查是否可以进行 upscale
        checkCanUpscaleAndVariation(parentRoomMidjourneyMsgDO, midjourneyProperties);
        // 判断是否已经 u 转换过
        boolean isUse = MjRoomMessageUtil.isUpscaleUse(parentRoomMidjourneyMsgDO.getUUseBit(), index, MidjourneyMsgActionEnum.UPSCALE);
        if (isUse) {
            throw new ServiceException("该消息已经进行过 U" + index);
        }

        // 查询是否存在转换过的消息
        RoomMidjourneyMsgService roomMidjourneyMsgService = SpringUtil.getBean(RoomMidjourneyMsgService.class);

        // 理论上这些状态的只会存在一条记录，不考虑并发插入的情况
        RoomMidjourneyMsgDO convertRoomMidjourneyMsgDO = roomMidjourneyMsgService.getOne(new LambdaQueryWrapper<RoomMidjourneyMsgDO>().eq(RoomMidjourneyMsgDO::getUvParentId, parentRoomMidjourneyMsgDO.getId())
                .in(RoomMidjourneyMsgDO::getStatus, MidjourneyMsgStatusEnum.SYS_SUCCESS, MidjourneyMsgStatusEnum.MJ_SUCCESS, MidjourneyMsgStatusEnum.MJ_FAILURE,
                        MidjourneyMsgStatusEnum.SYS_QUEUING, MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED, MidjourneyMsgStatusEnum.MJ_IN_PROGRESS,
                        MidjourneyMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE, MidjourneyMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE)
                // 指定位置
                .eq(RoomMidjourneyMsgDO::getUvIndex, index)
                // 回答
                .eq(RoomMidjourneyMsgDO::getType, MessageTypeEnum.ANSWER)
                // u 转换
                .eq(RoomMidjourneyMsgDO::getAction, MidjourneyMsgActionEnum.UPSCALE));
        if (Objects.isNull(convertRoomMidjourneyMsgDO)) {
            return;
        }
        MidjourneyMsgStatusEnum convertStatus = convertRoomMidjourneyMsgDO.getStatus();
        if (convertStatus == MidjourneyMsgStatusEnum.SYS_QUEUING) {
            // 理论不会进入
            throw new ServiceException(StrUtil.format("该 U{} 操作正在排队中，请勿重复操作", index));
        }
        if (convertStatus == MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED || convertStatus == MidjourneyMsgStatusEnum.MJ_IN_PROGRESS) {
            // 理论不会进入
            throw new ServiceException(StrUtil.format("该 U{} 操作正在处理中，请勿重复操作", index));
        }
        if (convertStatus == MidjourneyMsgStatusEnum.SYS_SUCCESS || convertStatus == MidjourneyMsgStatusEnum.MJ_SUCCESS) {
            // 理论上不会进入
            throw new ServiceException(StrUtil.format("该 U{} 操作已经成功，请勿重复操作", index));
        }
        // 失败过不重试，可能在 Discord 是已经成功的，减少出错，这两种状态在 MJ 可能是成功的。SYS_SEND_MJ_REQUEST_FAILURE 状态暂不考虑
        if (convertStatus == MidjourneyMsgStatusEnum.MJ_FAILURE || convertStatus == MidjourneyMsgStatusEnum.SYS_WAIT_MJ_RECEIVED_FAILURE) {
            throw new ServiceException(StrUtil.format("该 U{} 操作曾经失败过，无法再次进行操作", index));
        }
    }

    /**
     * 检查是否可以进行 variation
     *
     * @param parentRoomMidjourneyMsgDO 父消息
     * @param midjourneyProperties      Midjourney 配置
     */
    public static void checkCanVariation(RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO, MidjourneyProperties midjourneyProperties) {
        // 检查是否可以进行 upscale
        checkCanUpscaleAndVariation(parentRoomMidjourneyMsgDO, midjourneyProperties);
    }

    /**
     * 检查是否可以进行 upscale 和 variation
     *
     * @param parentRoomMidjourneyMsgDO 父消息
     * @param midjourneyProperties      Midjourney 配置
     */
    public static void checkCanUpscaleAndVariation(RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO, MidjourneyProperties midjourneyProperties) {
        if (Objects.isNull(parentRoomMidjourneyMsgDO)) {
            throw new ServiceException("消息不存在");
        }
        // 只有答案消息 且 状态为成功的消息 且 动作为 imagine 的消息才能进行 u 转换
        if (parentRoomMidjourneyMsgDO.getType() != MessageTypeEnum.ANSWER
                || parentRoomMidjourneyMsgDO.getAction() != MidjourneyMsgActionEnum.IMAGINE
                || parentRoomMidjourneyMsgDO.getStatus() != MidjourneyMsgStatusEnum.MJ_SUCCESS) {
            throw new ServiceException("该图片无法进行操作，原图未创建成功");
        }
        if (ObjectUtil.notEqual(parentRoomMidjourneyMsgDO.getDiscordChannelId(), midjourneyProperties.getChannelId())) {
            throw new ServiceException("由于 Discord 频道切换，该图无法进行转换操作，请重新生成");
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
