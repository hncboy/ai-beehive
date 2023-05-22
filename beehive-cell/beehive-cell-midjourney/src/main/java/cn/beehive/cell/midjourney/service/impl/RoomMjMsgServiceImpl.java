package cn.beehive.cell.midjourney.service.impl;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.base.enums.MjMsgStatusEnum;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.mapper.RoomMjMsgMapper;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.domain.query.RoomMjMsgCursorQuery;
import cn.beehive.cell.midjourney.domain.request.MjConvertRequest;
import cn.beehive.cell.midjourney.domain.request.MjDescribeRequest;
import cn.beehive.cell.midjourney.domain.request.MjImagineRequest;
import cn.beehive.cell.midjourney.domain.vo.RoomMjMsgVO;
import cn.beehive.cell.midjourney.handler.MjRoomMessageHandler;
import cn.beehive.cell.midjourney.handler.MjTaskQueueHandler;
import cn.beehive.cell.midjourney.service.DiscordService;
import cn.beehive.cell.midjourney.service.RoomMjMsgService;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息业务接口实现类
 */
@Service
public class RoomMjMsgServiceImpl extends ServiceImpl<RoomMjMsgMapper, RoomMjMsgDO> implements RoomMjMsgService {

    @Resource
    private DiscordService discordService;

    @Resource
    private MidjourneyConfig midjourneyConfig;

    @Resource
    private MjTaskQueueHandler mjTaskQueueHandler;

    @Override
    public List<RoomMjMsgVO> list(RoomMjMsgCursorQuery cursorQuery) {
        return null;
    }

    @Override
    public void imagine(MjImagineRequest imagineRequest) {
        // 检查是否有正在处理的任务
        MjRoomMessageHandler.checkExistProcessingTask();

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        // 问题消息创建插入
        RoomMjMsgDO questionMessage = new RoomMjMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(imagineRequest.getRoomId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setPrompt(imagineRequest.getPrompt());
        // 组装最终的 prompt
        questionMessage.setFinalPrompt("[".concat(String.valueOf(answerMessageId)).concat("] ").concat(questionMessage.getPrompt()));
        questionMessage.setAction(MjMsgActionEnum.IMAGINE);
        questionMessage.setStatus(MjMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setDiscordChannelId(midjourneyConfig.getChannelId());
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 创建回答消息
        createImagineAnswerMessage(questionMessage, answerMessageId);
    }

    /**
     * 创建 imagine 回答消息
     *
     * @param questionMessage 问题消息
     * @param answerMessageId 回答消息 id
     */
    private void createImagineAnswerMessage(RoomMjMsgDO questionMessage, Long answerMessageId) {
        // 创建任务并返回回答的状态
        MjMsgStatusEnum answerStatus = mjTaskQueueHandler.pushNewTask(answerMessageId);

        // 答案消息创建插入
        RoomMjMsgDO answerMessage = new RoomMjMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setUserId(FrontUserUtil.getUserId());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setFinalPrompt(questionMessage.getFinalPrompt());
        answerMessage.setType(MessageTypeEnum.ANSWER);
        answerMessage.setAction(MjMsgActionEnum.IMAGINE);
        answerMessage.setStatus(answerStatus);
        answerMessage.setDiscordChannelId(midjourneyConfig.getChannelId());
        answerMessage.setUvUseBit(0);
        answerMessage.setIsDeleted(false);

        // 达到队列上限
        if (answerStatus == MjMsgStatusEnum.SYS_MAX_QUEUE) {
            answerMessage.setResponseContent(StrUtil.format("当前排队任务为 {} 条，已经达到上限，请稍后再试", midjourneyConfig.getMaxWaitQueueSize()));
        }
        save(answerMessage);

        // 调用 discord 接口
        if (answerStatus == MjMsgStatusEnum.MJ_WAIT_RECEIVED) {
            Pair<Boolean, String> imagineResultPair = discordService.imagine(questionMessage.getFinalPrompt());
            // 调用失败的情况，应该是少数情况，这里不重试
            if (!imagineResultPair.getKey()) {
                answerMessage.setStatus(MjMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE);
                answerMessage.setResponseContent("系统异常，直接调用 imagine 接口失败，请稍后再试");
                answerMessage.setFailureReason(imagineResultPair.getValue());
                updateById(answerMessage);

                // 结束执行中任务
                mjTaskQueueHandler.finishExecuteTask(answerMessageId);
            }
        }
    }

    @Override
    public void upscale(MjConvertRequest convertRequest) {
        // TODO 重复 upscale discord 会怎么报错
        // TODO 可能要加分布式锁

        // 检查是否有正在处理的任务
        MjRoomMessageHandler.checkExistProcessingTask();
        // 获取原消息
        RoomMjMsgDO parentRoomMjMsgDO = getOne(new LambdaQueryWrapper<RoomMjMsgDO>().eq(RoomMjMsgDO::getId, convertRequest.getMsgId())
                .eq(RoomMjMsgDO::getRoomId, convertRequest.getRoomId())
                .eq(RoomMjMsgDO::getUserId, FrontUserUtil.getUserId()));
        // 检查是否可以 upscale
        MjRoomMessageHandler.checkCanUpscale(parentRoomMjMsgDO, convertRequest, midjourneyConfig);

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        // 问题消息创建插入
        RoomMjMsgDO questionMessage = new RoomMjMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(parentRoomMjMsgDO.getRoomId());
        questionMessage.setUserId(parentRoomMjMsgDO.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setPrompt(parentRoomMjMsgDO.getPrompt());
        questionMessage.setFinalPrompt(parentRoomMjMsgDO.getFinalPrompt());
        questionMessage.setUvParentId(parentRoomMjMsgDO.getId());
        questionMessage.setUvIndex(convertRequest.getIndex());
        questionMessage.setDiscordMessageId(parentRoomMjMsgDO.getDiscordMessageId());
        questionMessage.setAction(MjMsgActionEnum.UPSCALE);
        questionMessage.setStatus(MjMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 创建任务并返回回答的状态
        MjMsgStatusEnum answerStatus = mjTaskQueueHandler.pushNewTask(answerMessageId);

        // 答案消息创建插入
        RoomMjMsgDO answerMessage = new RoomMjMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setUserId(FrontUserUtil.getUserId());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setFinalPrompt(questionMessage.getFinalPrompt());
        answerMessage.setType(MessageTypeEnum.ANSWER);
        answerMessage.setAction(MjMsgActionEnum.UPSCALE);
        answerMessage.setUvParentId(parentRoomMjMsgDO.getId());
        answerMessage.setUvIndex(convertRequest.getIndex());
        // 这里先赋值，因为回调监听没有可以赋值的地方
        answerMessage.setDiscordStartTime(new Date());
        answerMessage.setStatus(answerStatus);
        answerMessage.setUvUseBit(0);
        answerMessage.setIsDeleted(false);

        // 达到队列上限
        if (answerStatus == MjMsgStatusEnum.SYS_MAX_QUEUE) {
            answerMessage.setResponseContent(StrUtil.format("当前排队任务为 {} 条，已经达到上限，请稍后再试", midjourneyConfig.getMaxWaitQueueSize()));
        }
        save(answerMessage);

        // 调用 discord 接口
        if (answerStatus == MjMsgStatusEnum.MJ_WAIT_RECEIVED) {
            Pair<Boolean, String> imagineResultPair = discordService.upscale(questionMessage.getDiscordMessageId(), convertRequest.getIndex(), MjRoomMessageHandler.getDiscordMessageHash(parentRoomMjMsgDO.getDiscordImageUrl()));
            // 调用失败的情况，应该是少数情况，这里不重试
            if (!imagineResultPair.getKey()) {
                answerMessage.setStatus(MjMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE);
                answerMessage.setResponseContent("系统异常，直接调用 upscale 接口失败，请稍后再试");
                answerMessage.setFailureReason(imagineResultPair.getValue());
                updateById(answerMessage);

                // 结束执行中任务
                mjTaskQueueHandler.finishExecuteTask(answerMessageId);
            }
        }
    }

    @Override
    public void variation(MjConvertRequest convertRequest) {
    }

    @Override
    public void describe(MjDescribeRequest describeRequest) {
    }
}
