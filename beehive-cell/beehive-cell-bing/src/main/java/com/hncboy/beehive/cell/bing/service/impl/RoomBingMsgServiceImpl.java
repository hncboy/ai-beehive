package com.hncboy.beehive.cell.bing.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.config.ProxyConfig;
import com.hncboy.beehive.base.domain.entity.RoomBingMsgDO;
import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.handler.mp.BeehiveServiceImpl;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.base.mapper.RoomBingMsgMapper;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.cell.bing.domain.bo.BingRoomBO;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.hncboy.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import com.hncboy.beehive.cell.bing.enums.BingCellConfigCodeEnum;
import com.hncboy.beehive.cell.bing.handler.BingApiResponseTypeMessageHandler;
import com.hncboy.beehive.cell.bing.handler.BingCellConfigStrategy;
import com.hncboy.beehive.cell.bing.handler.BingRoomHandler;
import com.hncboy.beehive.cell.bing.handler.converter.RoomBingMsgConverter;
import com.hncboy.beehive.cell.bing.service.RoomBingMsgService;
import com.hncboy.beehive.cell.bing.service.RoomBingService;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/26
 * NewBing 房间消息业务接口实现类
 */
@Slf4j
@Service
public class RoomBingMsgServiceImpl extends BeehiveServiceImpl<RoomBingMsgMapper, RoomBingMsgDO> implements RoomBingMsgService {

    /**
     * 消息分隔符
     */
    private static final String DELIMITER = "\u001E";

    /**
     * 最大失败重试次数
     */
    private static final int MAX_FAILURE_RETRIES = 3;

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private RoomBingService roomBingService;

    @Resource
    private BingCellConfigStrategy bingCellConfigStrategy;

    @Override
    public List<RoomBingMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomBingMsgDO> roomBingMsgDOList = cursorList(cursorQuery, RoomBingMsgDO::getId, new LambdaQueryWrapper<RoomBingMsgDO>()
                .eq(RoomBingMsgDO::getUserId, FrontUserUtil.getUserId())
                .eq(RoomBingMsgDO::getRoomId, cursorQuery.getRoomId()));
        return RoomBingMsgConverter.INSTANCE.entityToVo(roomBingMsgDOList);
    }

    @Override
    public ResponseBodyEmitter send(RoomBingMsgSendRequest sendRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("NewBing 请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("NewBing 请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取一个房间
        BingRoomBO bingRoomBO = roomBingService.getRoom(sendRequest.getRoomId(), sendRequest);

        // 保存问题消息
        RoomBingMsgDO questionMessage = RoomBingMsgConverter.INSTANCE.bingRoomBoToEntity(bingRoomBO);
        questionMessage.setIp(WebUtil.getIp());
        questionMessage.setType(MessageTypeEnum.QUESTION);
        questionMessage.setContent(sendRequest.getContent());
        save(questionMessage);

        try {
            // 建立 WebSocket 连接并发送消息
            buildWebSocketAndSendMessage(questionMessage, bingRoomBO, sendRequest, emitter, MAX_FAILURE_RETRIES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return emitter;
    }

    /**
     * 建立 WebSocket 连接并发送消息
     *
     * @param questionMessage        问题消息
     * @param bingRoomBO             房间业务信息
     * @param roomBingMsgSendRequest 发送消息请求
     * @param responseBodyEmitter    响应
     * @param retries                重试次数
     * @throws Exception 异常
     */
    private void buildWebSocketAndSendMessage(RoomBingMsgDO questionMessage, BingRoomBO bingRoomBO, RoomBingMsgSendRequest roomBingMsgSendRequest,
                                              ResponseBodyEmitter responseBodyEmitter, int retries) throws Exception {
        Map<BingCellConfigCodeEnum, DataWrapper> roomConfigParamMap = bingCellConfigStrategy.getRoomConfigParamAsMap(questionMessage.getRoomId());

        // 连接地址
        URI uri = new URI(roomConfigParamMap.get(BingCellConfigCodeEnum.WSS_URL).asString());

        // 请求头
        Map<String, String> httpHeaders = new HashMap<>(4);
        httpHeaders.put("x-forwarded-for", BingRoomHandler.generateRandomIp());
        httpHeaders.put("Cookie", roomConfigParamMap.get(BingCellConfigCodeEnum.COOKIE).asString());

        // 建立 WebSocket 连接，发送一句后就 close
        WebSocketClient webSocketClient = new WebSocketClient(uri, httpHeaders) {

            private ResponseBodyEmitter emitter = null;

            private BingApiResponseTypeMessageHandler bingApiResponseTypeMessageHandler;

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                // 第一句
                send("{\"protocol\":\"json\",\"version\":1}" + DELIMITER);
                log.debug("NewBing 房间：{}，请求参数：{}，WebSocket 连接成功", bingRoomBO.getRoomBingDO().getRoomId(), ObjectMapperUtil.toJson(roomBingMsgSendRequest));

                this.emitter = responseBodyEmitter;
                this.bingApiResponseTypeMessageHandler = SpringUtil.getBean(BingApiResponseTypeMessageHandler.class);
            }

            @Override
            public void onMessage(String message) {
                log.debug("NewBing 房间：{}，收到消息：{}", bingRoomBO.getRoomBingDO().getRoomId(), message);
                String[] events = message.split(DELIMITER);
                for (String event : events) {
                    // 空 JSON={}，用来响应第一句消息，这里发送问题
                    if (StrUtil.isBlank(event) || event.equals(StrPool.EMPTY_JSON)) {
                        send(BingRoomHandler.buildBingChatRequest(bingRoomBO.getRoomBingDO(), roomBingMsgSendRequest) + DELIMITER);
                        continue;
                    }

                    // 解析 type
                    int type = ObjectMapperUtil.readTree(event).get("type").asInt();
                    if (type == 1) {
                        bingApiResponseTypeMessageHandler.handleType1(message, emitter);
                        return;
                    }

                    if (type == 2) {
                        // 处理 type2 消息并返回是否开启新话题
                        boolean isNewTopic = bingApiResponseTypeMessageHandler.handleType2(questionMessage, bingRoomBO, message, emitter);
                        // 不管结果咋样都先关闭当前连接
                        close();
                        // 不需要开启新话题，直接结束
                        if (!isNewTopic) {
                            emitter.complete();
                            return;
                        }

                        // 需要开启新话题，重试次数大于 0 再次尝试连接
                        if (retries > 0) {
                            log.debug("NewBing 房间：{}，尝试第 {} 次重新建立话题", bingRoomBO.getRoomBingDO().getRoomId(), MAX_FAILURE_RETRIES - retries + 1);
                            // 刷新房间重新建立连接
                            try {
                                buildWebSocketAndSendMessage(questionMessage, bingRoomBO, roomBingMsgSendRequest, responseBodyEmitter, retries - 1);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            log.warn("NewBing 房间：{}，尝试重新建立话题失败，重试次数已用完", bingRoomBO.getRoomBingDO().getRoomId());
                            ResponseBodyEmitterUtil.send(emitter, R.fail("发送对话失败，请稍后重试。"));
                            emitter.complete();
                        }
                    }
                }
            }

            @Override
            public void onClose(int i, String s, boolean remote) {
                log.debug("NewBing 房间：{}，WebSocket 连接关闭", bingRoomBO.getRoomBingDO().getRoomId());

                // 没有开启连接的情况，网络问题出连接不了
                if (Objects.isNull(emitter)) {
                    emitter = responseBodyEmitter;
                    ResponseBodyEmitterUtil.send(emitter, R.fail("发送对话出现问题，请检查网络"));
                    emitter.complete();
                }
            }

            @Override
            public void onError(Exception e) {
                log.error("NewBing 房间：{}，WebSocket 连接发生错误", bingRoomBO.getRoomBingDO().getRoomId(), e);
                ResponseBodyEmitterUtil.send(emitter, R.fail("发送对话异常，请稍后重试。"));
                emitter.complete();
            }
        };

        // 设置代理
        webSocketClient.setProxy(proxyConfig.getProxy());

        // 阻塞连接
        webSocketClient.connectBlocking();
    }
}
