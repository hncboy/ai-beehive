package cn.beehive.cell.bing.service.impl;

import cn.beehive.base.domain.entity.RoomBingMsgDO;
import cn.beehive.base.domain.query.RoomMsgCursorQuery;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.handler.mp.BeehiveServiceImpl;
import cn.beehive.base.mapper.RoomBingMsgMapper;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ObjectMapperUtil;
import cn.beehive.base.util.ResponseBodyEmitterUtil;
import cn.beehive.base.util.WebUtil;
import cn.beehive.cell.bing.domain.bo.BingRoomBO;
import cn.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import cn.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import cn.beehive.cell.bing.handler.BingApiResponseTypeMessageHandler;
import cn.beehive.cell.bing.handler.BingRoomHandler;
import cn.beehive.cell.bing.handler.converter.RoomBingMsgConverter;
import cn.beehive.cell.bing.service.RoomBingMsgService;
import cn.beehive.cell.bing.service.RoomBingService;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private RoomBingService roomBingService;

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
     * @param questionMessage      问题消息
     * @param bingRoomBO             房间业务信息
     * @param roomBingMsgSendRequest 发送消息请求
     * @param responseBodyEmitter    响应
     * @param retries                重试次数
     * @throws Exception 异常
     */
    private void buildWebSocketAndSendMessage(RoomBingMsgDO questionMessage, BingRoomBO bingRoomBO, RoomBingMsgSendRequest roomBingMsgSendRequest,
                                              ResponseBodyEmitter responseBodyEmitter, int retries) throws Exception {
        // 连接地址
        URI uri = new URI("wss://sydney.bing.com/sydney/ChatHub");

        // 请求头
        Map<String, String> httpHeaders = new HashMap<>(2) {{
            // 无法直接访问，不加这个无法建立连接
            put("x-forwarded-for", BingRoomHandler.generateRandomIp());
            put("cookie", "SRCHD=AF=ANSPA1; SRCHUID=V=2&GUID=2BE42B7C692A42AAAB49046FB9E553E2&dmnchg=1; ANON=A=B7BA2C6E4A4579E4DBB6AC23FFFFFFFF; _EDGE_V=1; MUID=38A17C177DF2677216E46DDC7C3266B7; MUIDB=38A17C177DF2677216E46DDC7C3266B7; MicrosoftApplicationsTelemetryDeviceId=00b8d6cf-ad86-42b8-99cd-50d32b9ac546; MMCASM=ID=A7A5A2CD6BF646989799ECEF3C032424; _UR=OMD=13300423214&QS=4&TQS=4; _HPVN=CS=eyJQbiI6eyJDbiI6NywiU3QiOjEsIlFzIjowLCJQcm9kIjoiUCJ9LCJTYyI6eyJDbiI6NywiU3QiOjAsIlFzIjowLCJQcm9kIjoiSCJ9LCJReiI6eyJDbiI6NywiU3QiOjAsIlFzIjowLCJQcm9kIjoiVCJ9LCJBcCI6dHJ1ZSwiTXV0ZSI6dHJ1ZSwiTGFkIjoiMjAyMy0wMy0xOFQwMDowMDowMFoiLCJJb3RkIjoxLCJHd2IiOjAsIkRmdCI6bnVsbCwiTXZzIjowLCJGbHQiOjAsIkltcCI6MzJ9; ABDEF=V=13&ABDV=11&MRNB=1680431660668&MRB=0; _clck=clijyq|1|fai|0; _EDGE_S=SID=19624BA8B594641808E2588AB4EE656D; WLS=C=3c98cfa9c7dbfacc&N=boy; SUID=A; SRCHUSR=DOB=20220623&T=1685546931000; _SS=SID=19624BA8B594641808E2588AB4EE656D&OCID=MY02A4&R=2684&RB=2684&GB=0&RG=0&RP=2675; ipv6=hit=1685550536829&t=4; USRLOC=HS=1&ELOC=LAT=30.33444595336914|LON=120.18440246582031|N=%E6%8B%B1%E5%A2%85%E5%8C%BA%EF%BC%8C%E6%B5%99%E6%B1%9F%E7%9C%81|ELT=2|&DLOC=LAT=30.319443|LON=120.162398|A=60|N=Hangzhou%e6%8b%b1%e5%a2%85%e5%8c%ba|C=|S=|TS=230531152857|ETS=230531153857|; GC=J7RN5VoR2jeYfTbZ_-1OF8wRL1bSw3EGvjCsVk62ven3BxOVMXvFJTyR4qyRZilE2M48NstJj0lqw8uWFtcXjQ; _RwBf=ilt=6&ihpd=0&ispd=0&rc=2684&rb=2684&gb=0&rg=0&pc=2675&mtu=0&rbb=0.0&g=0&cid=&clo=0&v=1&l=2023-05-31T07:00:00.0000000Z&lft=0001-01-01T00:00:00.0000000&aof=0&o=16&p=bingcopilotwaitlist&c=MY00IA&t=4072&s=2023-02-12T05:52:27.0568531+00:00&ts=2023-05-31T15:28:53.9043033+00:00&rwred=0&wls=2&lka=0&lkt=0&TH=&mta=0&e=aSDrfaTnyj0a2Fa-w6XA7izL5YIJDYi5Um4OLGVjOZ0MOWMl_I2ONbA-9mYvBdNnTuic6E9YUmF1RBYUkLHNRA&A=B7BA2C6E4A4579E4DBB6AC23FFFFFFFF&mte=0; _U=1vRh7IWSMzMV4XHCEQiT4Gi7e737SR6iYI7-hQBEBlfHoPn5vlmwEkHfQe63Ki-IFimiu28RrAiMdMo2ctcPZJJXp-kNjUWiucnH1hbwkUmf42hWJmDpR6uKpK3DPAw3F_HI9dsOAm1HzQEXCktkmNoRZTA7hmZXdsIYZNoBu-zAh9e2DL8U0q9x1TvC8TEXaFLuGmTX_gxnzbTwvvDipVg; SRCHHPGUSR=SRCHLANG=zh-Hans&BZA=0&BRW=XW&BRH=S&CW=1482&CH=457&SW=1536&SH=865&DPR=1.3&UTC=480&DM=1&EXLTT=31&HV=1685546935&PV=15.0.0&WTS=63791668507&SCW=1482&SCH=2922&PRVCW=1482&PRVCH=391&THEME=1&cdxwinturn=2&cdxtone=Precise&cdxtoneopts=h3precise,clgalileo,gencontentv3");
        }};

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
                            ResponseBodyEmitterUtil.send(emitter, "发送对话失败，请稍后重试。");
                            emitter.complete();
                        }
                    }
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                log.debug("NewBing 房间：{}，WebSocket 连接关闭", bingRoomBO.getRoomBingDO().getRoomId());
            }

            @Override
            public void onError(Exception e) {
                log.error("NewBing 房间：{}，WebSocket 连接发生错误", bingRoomBO.getRoomBingDO().getRoomId(), e);
                ResponseBodyEmitterUtil.send(emitter, "发送对话异常，请稍后重试。");
                emitter.complete();
            }
        };
        // 阻塞连接
        webSocketClient.connectBlocking();
    }
}
