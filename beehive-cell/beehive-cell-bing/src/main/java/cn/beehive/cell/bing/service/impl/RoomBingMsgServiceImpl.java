package cn.beehive.cell.bing.service.impl;

import cn.beehive.base.domain.entity.RoomBingDO;
import cn.beehive.base.domain.entity.RoomBingMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.mapper.RoomBingMsgMapper;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.cell.bing.domain.bo.BingApiSendMessageResultBO;
import cn.beehive.cell.bing.domain.bo.BingApiSendThrottlingResultBO;
import cn.beehive.cell.bing.domain.bo.BingApiSendType1ResultBO;
import cn.beehive.cell.bing.domain.bo.BingApiSendType2ResultBO;
import cn.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import cn.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import cn.beehive.cell.bing.domain.vo.RoomBingStreamMsgVO;
import cn.beehive.cell.bing.handler.BingApiRequestHandler;
import cn.beehive.cell.bing.handler.converter.RoomBingMsgConverter;
import cn.beehive.cell.bing.service.RoomBingMsgService;
import cn.beehive.cell.bing.service.RoomBingService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
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
public class RoomBingMsgServiceImpl extends ServiceImpl<RoomBingMsgMapper, RoomBingMsgDO> implements RoomBingMsgService {

    /**
     * 消息分隔符
     */
    private static final String DELIMITER = "\u001E";

    @Resource
    private RoomBingService roomBingService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<RoomBingMsgVO> list(RoomMsgCursorQuery cursorQuery) {
        List<RoomBingMsgDO> roomBingMsgDOList = list(new LambdaQueryWrapper<RoomBingMsgDO>()
                .eq(RoomBingMsgDO::getRoomId, cursorQuery.getRoomId())
                .gt(cursorQuery.getIsUseCursor() && cursorQuery.getIsAsc(), RoomBingMsgDO::getId, cursorQuery.getCursor())
                .lt(cursorQuery.getIsUseCursor() && !cursorQuery.getIsAsc(), RoomBingMsgDO::getId, cursorQuery.getCursor())
                .last(StrUtil.format("limit {}", cursorQuery.getSize()))
                .orderBy(true, cursorQuery.getIsAsc(), RoomBingMsgDO::getId));
        return RoomBingMsgConverter.INSTANCE.entityToVO(roomBingMsgDOList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseBodyEmitter send(RoomBingMsgSendRequest sendRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("NewBing 请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));
        emitter.onTimeout(() -> log.error("NewBing 请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(sendRequest)));

        // 获取一个房间
        RoomBingDO roomBingDO = roomBingService.getRoom(sendRequest.getRoomId());

        try {
            // 建立 WebSocket 连接并发送消息
            buildWebSocketAndSendMessage(roomBingDO, sendRequest, emitter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return emitter;
    }

    /**
     * 建立 WebSocket 连接并发送消息
     *
     * @param roomBingDO             房间信息
     * @param roomBingMsgSendRequest 发送消息请求
     * @param responseBodyEmitter    响应
     * @throws Exception 异常
     */
    private void buildWebSocketAndSendMessage(RoomBingDO roomBingDO, RoomBingMsgSendRequest roomBingMsgSendRequest, ResponseBodyEmitter responseBodyEmitter) throws Exception {
        // 连接地址
        URI uri = new URI("wss://sydney.bing.com/sydney/ChatHub");

        // 请求头
        Map<String, String> httpHeaders = new HashMap<>(4) {{
            // 无法直接访问，不加这个无法建立连接
            put("x-forwarded-for", BingApiRequestHandler.generateRandomIp());
        }};

        // 建立 WebSocket 连接，发送一句后就 close
        WebSocketClient webSocketClient = new WebSocketClient(uri, httpHeaders) {

            private ResponseBodyEmitter emitter = null;

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                // 第一句
                send("{\"protocol\":\"json\",\"version\":1}" + DELIMITER);
                System.out.println("握手成功");
                this.emitter = responseBodyEmitter;
            }

            @Override
            public void onMessage(String message) {
                System.out.println("收到消息" + message);
                String[] events = message.split(DELIMITER);
                for (String event : events) {
                    // 空 JSON={}，用来响应第一句消息，这里发送问题
                    if (StrUtil.isBlank(event) || event.equals(StrPool.EMPTY_JSON)) {
                        send(BingApiRequestHandler.buildChatRequest(roomBingDO, roomBingMsgSendRequest) + DELIMITER);
                        continue;
                    }

                    int type;
                    try {
                        // 解析 type
                        type = objectMapper.readTree(event).get("type").asInt();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    if (type == 1) {
                        handleType1Response(message, emitter);
                    } else if (type == 2) {
                        handleType2Response(roomBingDO, message, emitter);
                        // 关闭连接
                        close();
                    }
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("链接已关闭");
                emitter.complete();
            }

            @Override
            public void onError(Exception e) {
                System.out.println("发生错误已关闭");
                emitter.complete();
            }
        };
        // 阻塞连接
        webSocketClient.connectBlocking();
    }

    /**
     * type=1
     * 流式响应，包含了聊天内容
     *
     * @param receiveMessage 响应结果
     * @param emitter        响应流
     */
    private void handleType1Response(String receiveMessage, ResponseBodyEmitter emitter) {
        BingApiSendType1ResultBO resultBO = ObjectMapperUtil.fromJson(receiveMessage, BingApiSendType1ResultBO.class);
        BingApiSendType1ResultBO.Argument argument = resultBO.getArguments().get(0);

        if (Objects.nonNull(argument.getMessages())) {
            String responseText = argument.getMessages().get(0).getText();
            try {
                emitter.send(RoomBingStreamMsgVO.builder().content(responseText).build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * type=2
     * 响应结果，包含正常和异常结束
     *
     * @param roomBingDO     房间信息
     * @param receiveMessage 响应结果
     * @param emitter        响应流
     */
    private void handleType2Response(RoomBingDO roomBingDO, String receiveMessage, ResponseBodyEmitter emitter) {
        BingApiSendType2ResultBO resultBO = ObjectMapperUtil.fromJson(receiveMessage, BingApiSendType2ResultBO.class);
        String resultValue = resultBO.getItem().getResult().getValue();

        // 成功情况
        if (Objects.equals(resultValue, "Success")) {
            List<BingApiSendMessageResultBO> messages = resultBO.getItem().getMessages();
            // 过滤出机器人回复的那一条
            List<BingApiSendMessageResultBO> botMessages = messages.stream().filter(message -> Objects.equals(message.getAuthor(), "bot")).toList();
            if (CollectionUtil.isEmpty(botMessages)) {
                System.out.println("bing 生气了，不想回复了，需要开启新话题");
                // TODO 开启新话题
                return;
            }

            // 获取回复消息
            BingApiSendMessageResultBO botMessage = botMessages.get(0);
            System.out.println("最终回复：" + botMessage.getText());

            RoomBingStreamMsgVO roomBingStreamMsgVO = new RoomBingStreamMsgVO();
            roomBingStreamMsgVO.setContent(botMessage.getText());

            // 提取建议
            List<BingApiSendMessageResultBO.SuggestedResponse> suggestedResponses = botMessage.getSuggestedResponses();
            if (CollectionUtil.isNotEmpty(suggestedResponses)) {
                roomBingStreamMsgVO.setSuggests(suggestedResponses.stream().map(BingApiSendMessageResultBO.SuggestedResponse::getText).toList());
            }

            // 提取用户提问次数，可能会发生变化，不清楚什么情况会变
            BingApiSendThrottlingResultBO throttling = resultBO.getItem().getThrottling();
            roomBingStreamMsgVO.setNumUserMessagesInConversation(throttling.getNumUserMessagesInConversation());
            roomBingStreamMsgVO.setMaxNumUserMessagesInConversation(throttling.getMaxNumUserMessagesInConversation());
            // 超过最大提问次数会一直回复：
            if (throttling.getNumUserMessagesInConversation() > throttling.getMaxNumUserMessagesInConversation()) {
                // TODO 开启新话题
                System.out.println("超过最大提问次数，需要开启新话题");
                return;
            }

            // 更新房间提问次数
            roomBingService.updateRoomMessageNum(roomBingDO, throttling);

            try {
                // 发送消息
                emitter.send(ObjectMapperUtil.toJson(roomBingStreamMsgVO));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("成功结束");
            return;
        }


        // TODO 开启新话题
        System.out.println("未记录的异常");
    }
}
