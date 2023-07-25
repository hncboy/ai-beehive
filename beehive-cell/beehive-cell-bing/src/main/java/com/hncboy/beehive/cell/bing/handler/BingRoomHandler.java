package com.hncboy.beehive.cell.bing.handler;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.ForestRequestUtil;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.bing.constant.NewBingConstant;
import com.hncboy.beehive.cell.bing.domain.bo.BingApiCreateConversationResultBO;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.hncboy.beehive.cell.bing.enums.BingCellConfigCodeEnum;
import com.hncboy.beehive.cell.bing.enums.BingModeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间相关处理
 */
@Slf4j
public class BingRoomHandler {

    private static final String CHAT_REQUEST_JSON_STR = ResourceUtil.readUtf8Str("bing/send.json");
    private static final Map<String, String> OPTIONS_JSON_MAP = new HashMap<>() {{
        put(BingModeEnum.CREATIVE.getName(), ResourceUtil.readUtf8Str("bing/options_creative.json"));
        put(BingModeEnum.BALANCE.getName(), ResourceUtil.readUtf8Str("bing/options_balance.json"));
        put(BingModeEnum.PRECISE.getName(), ResourceUtil.readUtf8Str("bing/options_precise.json"));
    }};

    /**
     * 创建 bing 对话
     *
     * @param roomId 放假 id
     * @return 创建结果
     */
    public static BingApiCreateConversationResultBO createConversation(Long roomId) {
        BingCellConfigStrategy  bingCellConfigStrategy = SpringUtil.getBean(BingCellConfigStrategy.class);
        Map<BingCellConfigCodeEnum, DataWrapper> roomConfigParamMap = bingCellConfigStrategy.getRoomConfigParamAsMap(roomId);

        ForestRequest<?> forestRequest = Forest.get(roomConfigParamMap.get(BingCellConfigCodeEnum.CREATE_CONVERSATION_URL).asString());
        ForestRequestUtil.buildProxy(forestRequest);
        forestRequest.addHeader("Cookie", roomConfigParamMap.get(BingCellConfigCodeEnum.CREATE_CONVERSATION_URL).asString());
        ForestResponse<?> forestResponse = forestRequest.execute(ForestResponse.class);
        if (forestResponse.isError()) {
            // 国内访问会 403
            log.warn("用户 {} 房间 {} 创建 NewBing 会话失败，响应结果：{}", FrontUserUtil.getUserId(), roomId, forestResponse.getContent(), forestResponse.getException());
            throw new ServiceException("创建 NewBing 会话失败，请稍后再试");
        }

        BingApiCreateConversationResultBO resultBO = ObjectMapperUtil.fromJson(forestResponse.getContent(), BingApiCreateConversationResultBO.class);
        if (ObjectUtil.notEqual(resultBO.getResult().getValue(), NewBingConstant.RESPONSE_SUCCESS)) {
            // 有时候会报 {"result":{"value":"UnauthorizedRequest","message":"Sorry, you need to login first to access this service."}} 此时可以让用户多试几次
            log.warn("用户 {} 房间 {} 创建 NewBing 会话异常，响应结果：{}", FrontUserUtil.getUserId(), roomId, forestResponse.getContent());
            throw new ServiceException("创建 NewBing 会话异常，请稍后再试");
        }
        return resultBO;
    }

    /**
     * 构建 Bing API 请求参数
     *
     * @param roomBingDO             房间信息
     * @param roomBingMsgSendRequest 用户请求
     * @return 请求参数
     */
    public static String buildBingChatRequest(RoomBingDO roomBingDO, RoomBingMsgSendRequest roomBingMsgSendRequest) {

        // 先替换字符串
        String sendStr = CHAT_REQUEST_JSON_STR
                .replace("$conversationId", roomBingDO.getConversationId())
                .replace("$conversationSignature", roomBingDO.getConversationSignature())
                .replace("$clientId", roomBingDO.getClientId())
                .replace("$prompt", roomBingMsgSendRequest.getContent());
        // 转换到 JsonNode
        JsonNode jsonNode = ObjectMapperUtil.readTree(sendStr);
        // 获取 "arguments" 数组的第一个元素，转换为 ObjectNode
        ObjectNode objectNode = (ObjectNode) jsonNode.get("arguments").get(0);

        // TODO 不同模式没生效，不清楚怎么改
        objectNode.set("optionsSets", ObjectMapperUtil.readTree(OPTIONS_JSON_MAP.get(roomBingDO.getMode())));

        // 替换 isStartOfSession，第一条消息为 true，其他为 false；如果第一条消息为 false 会报错，如果其他为 true，则会一直重复第一条的回答
        objectNode.put("isStartOfSession", roomBingDO.getNumUserMessagesInConversation() == 0);
        return ObjectMapperUtil.toJson(jsonNode);
    }

    /**
     * 生成随机 IP
     *
     * @return IP
     */
    public static String generateRandomIp() {
        int a = RandomUtil.randomInt(104, 108);
        int b = RandomUtil.randomInt(256);
        int c = RandomUtil.randomInt(256);
        return "13." + a + "." + b + "." + c;
    }
}
