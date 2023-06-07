package cn.beehive.cell.midjourney.service.impl;

import cn.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.base.enums.MidjourneyMsgStatusEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.handler.mp.BeehiveServiceImpl;
import cn.beehive.base.mapper.RoomMidjourneyMsgMapper;
import cn.beehive.base.util.FileUtil;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.core.hander.CellPermissionHandler;
import cn.beehive.cell.core.hander.RoomHandler;
import cn.beehive.cell.midjourney.domain.request.MjConvertRequest;
import cn.beehive.cell.midjourney.domain.request.MjDescribeRequest;
import cn.beehive.cell.midjourney.domain.request.MjImagineRequest;
import cn.beehive.cell.midjourney.domain.vo.RoomMidjourneyMsgVO;
import cn.beehive.cell.midjourney.handler.MidjourneyRoomMsgHandler;
import cn.beehive.cell.midjourney.handler.MidjourneyTaskQueueHandler;
import cn.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import cn.beehive.cell.midjourney.handler.converter.RoomMidjourneyMsgConverter;
import cn.beehive.cell.midjourney.service.DiscordSendService;
import cn.beehive.cell.midjourney.service.DiscordService;
import cn.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息业务接口实现类
 */
@Slf4j
@Service
public class RoomMidjourneyMsgServiceImpl extends BeehiveServiceImpl<RoomMidjourneyMsgMapper, RoomMidjourneyMsgDO> implements RoomMidjourneyMsgService {

    @Resource
    private DiscordService discordService;

    @Resource
    private MidjourneyProperties midjourneyProperties;

    @Resource
    private MidjourneyTaskQueueHandler midjourneyTaskQueueHandler;

    @Override
    public List<RoomMidjourneyMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomMidjourneyMsgDO> roomMidjourneyMsgDOList = cursorList(cursorQuery, RoomMidjourneyMsgDO::getId, new LambdaQueryWrapper<RoomMidjourneyMsgDO>()
                .eq(RoomMidjourneyMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomMidjourneyMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomMidjourneyMsgConverter.INSTANCE.entityToVO(roomMidjourneyMsgDOList);
    }

    @Override
    public void imagine(MjImagineRequest imagineRequest) {
        // 检查是否可以操作
        MidjourneyRoomMsgHandler.checkCanOperate(imagineRequest.getRoomId());

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        // 问题消息创建插入
        RoomMidjourneyMsgDO questionMessage = new RoomMidjourneyMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(imagineRequest.getRoomId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setPrompt(imagineRequest.getPrompt());
        // 组装最终的 prompt
        questionMessage.setFinalPrompt("[".concat(String.valueOf(answerMessageId)).concat("] ").concat(questionMessage.getPrompt()));
        questionMessage.setAction(MjMsgActionEnum.IMAGINE);
        questionMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setDiscordChannelId(midjourneyProperties.getChannelId());
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 初始化回答消息
        RoomMidjourneyMsgDO answerMessage = new RoomMidjourneyMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setFinalPrompt(questionMessage.getFinalPrompt());
        answerMessage.setAction(MjMsgActionEnum.IMAGINE);
        answerMessage.setUUseBit(0);

        // 创建回答消息
        createAnswerMessage(answerMessage, () -> discordService.imagine(answerMessage.getFinalPrompt()));
    }

    @Override
    public void upscale(MjConvertRequest convertRequest) {
        // 检查是否可以操作
        MidjourneyRoomMsgHandler.checkCanOperate(convertRequest.getRoomId());

        // 获取原消息
        RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO = getOne(new LambdaQueryWrapper<RoomMidjourneyMsgDO>().eq(RoomMidjourneyMsgDO::getId, convertRequest.getMsgId())
                .eq(RoomMidjourneyMsgDO::getRoomId, convertRequest.getRoomId())
                .eq(RoomMidjourneyMsgDO::getUserId, FrontUserUtil.getUserId()));
        // 检查是否可以 upscale
        MidjourneyRoomMsgHandler.checkCanUpscale(parentRoomMidjourneyMsgDO, convertRequest.getIndex(), midjourneyProperties);

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        // 问题消息创建插入
        RoomMidjourneyMsgDO questionMessage = new RoomMidjourneyMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(parentRoomMidjourneyMsgDO.getRoomId());
        questionMessage.setUserId(parentRoomMidjourneyMsgDO.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setPrompt(parentRoomMidjourneyMsgDO.getPrompt());
        questionMessage.setFinalPrompt(parentRoomMidjourneyMsgDO.getFinalPrompt());
        questionMessage.setUvParentId(parentRoomMidjourneyMsgDO.getId());
        questionMessage.setUvIndex(convertRequest.getIndex());
        questionMessage.setDiscordMessageId(parentRoomMidjourneyMsgDO.getDiscordMessageId());
        questionMessage.setAction(MjMsgActionEnum.UPSCALE);
        questionMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 初始化回答消息
        RoomMidjourneyMsgDO answerMessage = new RoomMidjourneyMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setFinalPrompt(questionMessage.getFinalPrompt());
        answerMessage.setAction(MjMsgActionEnum.UPSCALE);
        answerMessage.setUvParentId(parentRoomMidjourneyMsgDO.getId());
        answerMessage.setUvIndex(convertRequest.getIndex());
        // 这里先赋值，因为回调监听没有可以赋值的地方
        answerMessage.setDiscordStartTime(new Date());

        // 创建回答消息
        createAnswerMessage(answerMessage, () -> discordService.upscale(parentRoomMidjourneyMsgDO.getDiscordMessageId(), answerMessage.getUvIndex(), MidjourneyRoomMsgHandler.getDiscordMessageHash(parentRoomMidjourneyMsgDO.getDiscordImageUrl())));
    }

    @Override
    public void variation(MjConvertRequest convertRequest) {
        // 检查是否可以操作
        MidjourneyRoomMsgHandler.checkCanOperate(convertRequest.getRoomId());

        // 获取原消息
        RoomMidjourneyMsgDO parentRoomMidjourneyMsgDO = getOne(new LambdaQueryWrapper<RoomMidjourneyMsgDO>().eq(RoomMidjourneyMsgDO::getId, convertRequest.getMsgId())
                .eq(RoomMidjourneyMsgDO::getRoomId, convertRequest.getRoomId())
                .eq(RoomMidjourneyMsgDO::getUserId, FrontUserUtil.getUserId()));
        // 检查是否可以 variation
        MidjourneyRoomMsgHandler.checkCanVariation(parentRoomMidjourneyMsgDO, midjourneyProperties);

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        // 问题消息创建插入
        RoomMidjourneyMsgDO questionMessage = new RoomMidjourneyMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(parentRoomMidjourneyMsgDO.getRoomId());
        questionMessage.setUserId(parentRoomMidjourneyMsgDO.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setPrompt(parentRoomMidjourneyMsgDO.getPrompt());
        questionMessage.setFinalPrompt(parentRoomMidjourneyMsgDO.getFinalPrompt());
        questionMessage.setUvParentId(parentRoomMidjourneyMsgDO.getId());
        questionMessage.setUvIndex(convertRequest.getIndex());
        questionMessage.setDiscordMessageId(parentRoomMidjourneyMsgDO.getDiscordMessageId());
        questionMessage.setAction(MjMsgActionEnum.VARIATION);
        questionMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 初始化回答消息
        RoomMidjourneyMsgDO answerMessage = new RoomMidjourneyMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setFinalPrompt(questionMessage.getFinalPrompt());
        answerMessage.setAction(MjMsgActionEnum.VARIATION);
        answerMessage.setUvParentId(parentRoomMidjourneyMsgDO.getId());
        answerMessage.setUvIndex(convertRequest.getIndex());
        answerMessage.setDiscordStartTime(new Date());

        // 创建回答消息
        createAnswerMessage(answerMessage, () -> discordService.variation(parentRoomMidjourneyMsgDO.getDiscordMessageId(), answerMessage.getUvIndex(), MidjourneyRoomMsgHandler.getDiscordMessageHash(parentRoomMidjourneyMsgDO.getDiscordImageUrl())));
    }

    @Override
    public void describe(MjDescribeRequest describeRequest) {
        // 检查房间是否存在
        RoomHandler.checkRoomExistAndCellCanUse(describeRequest.getRoomId(), CellCodeEnum.MIDJOURNEY);

        // 这两个 id 按先后顺序生成，保证在表里的顺序也是有先后的
        // 生成问题的消息 id
        long questionMessageId = IdWorker.getId();
        // 生成回答消息的 id
        long answerMessageId = IdWorker.getId();

        MultipartFile multipartFile = describeRequest.getFile();
        // 新文件名：describe_ + 消息 id + 后缀
        String newFileName = "describe_" + answerMessageId + StrPool.DOT + FileUtil.getFileExtension(multipartFile.getOriginalFilename());
        // 保存文件
        FileUtil.downloadFromMultipartFile(multipartFile, midjourneyProperties.getImageLocation(), newFileName);

        // 判断文件大小
        if (multipartFile.getSize() > midjourneyProperties.getMaxFileSize()) {
            int maxMbFileSize = midjourneyProperties.getMaxFileSize() / 1024 / 1024;
            log.warn("Midjourney 业务异常，用户 id：{}，房间 id：{}，describe 文件大小为{}MB，超过{}MB限制", FrontUserUtil.getUserId(), describeRequest.getRoomId(), multipartFile.getSize(), maxMbFileSize);
            throw new ServiceException(StrUtil.format("文件大小超过{}MB限制", maxMbFileSize));
        }

        // 判断文件后缀是否符合
        if (!StrUtil.equalsAnyIgnoreCase(multipartFile.getContentType(), "image/jpeg", "image/png")) {
            log.warn("Midjourney 业务异常，用户 id：{}，房间 id：{}，describe 文件大小格式为 {} 错误", FrontUserUtil.getUserId(), describeRequest.getRoomId(), multipartFile.getContentType());
            throw new ServiceException("文件格式不符合要求，只能是 jpg 或 png 格式");
        }

        // 上传图片
        Pair<Boolean, String> uploadResponsePair = discordService.uploadImage(newFileName, multipartFile);
        if (!uploadResponsePair.getKey()) {
            throw new ServiceException(uploadResponsePair.getValue());
        }

        // 上面的操作应该没有并发限制，所以上面的上传图片操作就不考虑队列限制

        String discordUploadFileName = uploadResponsePair.getValue();
        // 检查是否有正在处理的任务
        MidjourneyRoomMsgHandler.checkExistProcessingTask();

        // 问题消息创建插入
        RoomMidjourneyMsgDO questionMessage = new RoomMidjourneyMsgDO();
        questionMessage.setId(questionMessageId);
        questionMessage.setRoomId(describeRequest.getRoomId());
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setImageName(newFileName);
        questionMessage.setDiscordImageUrl(discordUploadFileName);
        questionMessage.setAction(MjMsgActionEnum.DESCRIBE);
        questionMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SUCCESS);
        questionMessage.setDiscordChannelId(midjourneyProperties.getChannelId());
        questionMessage.setIsDeleted(false);
        save(questionMessage);

        // 初始化回答消息
        RoomMidjourneyMsgDO answerMessage = new RoomMidjourneyMsgDO();
        answerMessage.setId(answerMessageId);
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setImageName(newFileName);
        answerMessage.setDiscordImageUrl(discordUploadFileName);
        answerMessage.setAction(MjMsgActionEnum.DESCRIBE);
        answerMessage.setDiscordStartTime(new Date());
        answerMessage.setUUseBit(0);

        // 创建回答消息
        createAnswerMessage(answerMessage, () -> discordService.describe(answerMessage.getDiscordImageUrl()));
    }

    /**
     * 创建回答消息
     *
     * @param answerMessage      回答消息
     * @param discordSendService discord 发送服务接口
     */
    private void createAnswerMessage(RoomMidjourneyMsgDO answerMessage, DiscordSendService discordSendService) {
        // 填充公共字段
        answerMessage.setUserId(FrontUserUtil.getUserId());
        answerMessage.setType(MessageTypeEnum.ANSWER);
        answerMessage.setDiscordChannelId(midjourneyProperties.getChannelId());
        answerMessage.setIsDeleted(false);


        // 创建任务并返回回答的状态
        MidjourneyMsgStatusEnum answerStatus = midjourneyTaskQueueHandler.pushNewTask(answerMessage.getId());
        // 达到队列上限
        if (answerStatus == MidjourneyMsgStatusEnum.SYS_MAX_QUEUE) {
            answerMessage.setResponseContent(StrUtil.format("当前排队任务为 {} 条，已经达到上限，请稍后再试", midjourneyProperties.getMaxWaitQueueSize()));
        }
        answerMessage.setStatus(answerStatus);
        save(answerMessage);

        // 等待接收状态，此时可以调用 discord 接口
        if (answerStatus == MidjourneyMsgStatusEnum.MJ_WAIT_RECEIVED) {
            Pair<Boolean, String> resultPair = discordSendService.sendRequest();
            // 调用失败的情况，应该是少数情况，这里不重试
            if (!resultPair.getKey()) {
                answerMessage.setStatus(MidjourneyMsgStatusEnum.SYS_SEND_MJ_REQUEST_FAILURE);
                answerMessage.setResponseContent("系统异常，直接调用 discord 接口失败，请稍后再试");
                answerMessage.setFailureReason(resultPair.getValue());
                updateById(answerMessage);

                // 结束执行中任务
                midjourneyTaskQueueHandler.finishExecuteTask(answerMessage.getId());
            }
        }
    }
}
