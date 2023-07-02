package com.hncboy.beehive.cell.openai.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hncboy.beehive.base.domain.entity.RoomOpenAiImageMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.MessageStatusEnum;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.mapper.RoomOpenAiImageMsgMapper;
import com.hncboy.beehive.base.util.FileUtil;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.OkHttpClientUtil;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiImageSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiImageMsgVO;
import com.hncboy.beehive.cell.openai.enums.OpenAiImageCellConfigCodeEnum;
import com.hncboy.beehive.cell.openai.handler.converter.RoomOpenAiImageMsgConverter;
import com.hncboy.beehive.cell.openai.module.key.OpenAiApiKeyHandler;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiImageMsgService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.images.Image;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.entity.images.Item;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像房间消息业务实现类
 */
@Slf4j
@Service
public class RoomOpenAiImageMsgServiceImpl extends BeehiveServiceImpl<RoomOpenAiImageMsgMapper, RoomOpenAiImageMsgDO> implements RoomOpenAiImageMsgService {

    @Resource
    private CellConfigFactory cellConfigFactory;

    @Override
    public List<RoomOpenAiImageMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomOpenAiImageMsgDO> cursorList = cursorList(cursorQuery, RoomOpenAiImageMsgDO::getId, new LambdaQueryWrapper<RoomOpenAiImageMsgDO>()
                .eq(RoomOpenAiImageMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomOpenAiImageMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomOpenAiImageMsgConverter.INSTANCE.entityToVO(cursorList);
    }

    @Override
    public RoomOpenAiImageMsgVO send(RoomOpenAiImageSendRequest sendRequest) {
        // 获取房间配置参数
        CellConfigStrategy cellConfigStrategy = cellConfigFactory.getCellConfigStrategy(sendRequest.getRoomId(), CellCodeEnum.OPENAI_IMAGE);
        Map<OpenAiImageCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap = cellConfigStrategy.getRoomConfigParamAsMap(sendRequest.getRoomId());

        // 获取 ApiKey 相关信息
        Pair<String, String> chatApiKeyInfoPair = OpenAiApiKeyHandler.getImageApiKeyInfo(
                roomConfigParamAsMap.get(OpenAiImageCellConfigCodeEnum.API_KEY).asString(),
                roomConfigParamAsMap.get(OpenAiImageCellConfigCodeEnum.OPENAI_BASE_URL).asString(),
                roomConfigParamAsMap.get(OpenAiImageCellConfigCodeEnum.KEY_STRATEGY).asString());

        String apiKey = chatApiKeyInfoPair.getKey();

        // 构建图像生成参数
        Image image = Image.builder()
                .prompt(sendRequest.getPrompt())
                .size(roomConfigParamAsMap.get(OpenAiImageCellConfigCodeEnum.SIZE).asString())
                .build();

        // 创建问题消息
        RoomOpenAiImageMsgDO questionMessage = new RoomOpenAiImageMsgDO();
        questionMessage.setUserId(FrontUserUtil.getUserId());
        questionMessage.setRoomId(sendRequest.getRoomId());
        questionMessage.setMessageType(MessageTypeEnum.QUESTION);
        questionMessage.setApiKey(apiKey);
        questionMessage.setSize(image.getSize());
        questionMessage.setPrompt(sendRequest.getPrompt());
        questionMessage.setOriginalData(ObjectMapperUtil.toJson(image));
        questionMessage.setStatus(MessageStatusEnum.INIT);
        questionMessage.setRoomConfigParamJson(ObjectMapperUtil.toJson(roomConfigParamAsMap));
        save(questionMessage);

        // 构建 OpenAiClient
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(Collections.singletonList(apiKey))
                .okHttpClient(OkHttpClientUtil.getProxyInstance())
                .apiHost(chatApiKeyInfoPair.getValue())
                .build();

        // 构建回答消息
        RoomOpenAiImageMsgDO answerMessage = new RoomOpenAiImageMsgDO();
        answerMessage.setId(IdWorker.getId());
        answerMessage.setUserId(questionMessage.getUserId());
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setParentQuestionMessageId(questionMessage.getId());
        answerMessage.setMessageType(MessageTypeEnum.ANSWER);
        answerMessage.setApiKey(questionMessage.getApiKey());
        answerMessage.setSize(questionMessage.getSize());
        answerMessage.setPrompt(questionMessage.getPrompt());
        answerMessage.setRoomConfigParamJson(questionMessage.getRoomConfigParamJson());

        try {
            ImageResponse imageResponse = openAiClient.genImages(image);
            answerMessage.setOriginalData(ObjectMapperUtil.toJson(imageResponse));

            if (Objects.isNull(imageResponse) || CollUtil.isEmpty(imageResponse.getData())) {
                answerMessage.setResponseErrorData("生成图片数据为空");
                answerMessage.setStatus(MessageStatusEnum.FAILURE);
            } else {
                Item item = imageResponse.getData().get(0);
                answerMessage.setOpenaiImageUrl(item.getUrl());
                // 构建图片名称
                String imageName = "openaiImage-".concat(String.valueOf(answerMessage.getId())).concat(".png");
                // 下载图片
                FileUtil.downloadFromUrl(answerMessage.getOpenaiImageUrl(), imageName);
                answerMessage.setImageName(imageName);
                answerMessage.setStatus(MessageStatusEnum.SUCCESS);
            }
        } catch (Exception e) {
            log.error("OpenAi 图像生成异常", e);
            answerMessage.setResponseErrorData("图像生成异常：" + e.getMessage());
            answerMessage.setStatus(MessageStatusEnum.FAILURE);
        }

        // 保存回答消息
        save(answerMessage);
        questionMessage.setStatus(answerMessage.getStatus());
        // 更新问题消息
        updateById(questionMessage);

        return RoomOpenAiImageMsgConverter.INSTANCE.entityToVO(answerMessage);
    }
}
