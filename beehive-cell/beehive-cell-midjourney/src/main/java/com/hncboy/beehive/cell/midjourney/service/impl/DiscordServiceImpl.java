package com.hncboy.beehive.cell.midjourney.service.impl;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.Header;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hncboy.beehive.base.util.ForestRequestUtil;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import com.hncboy.beehive.cell.midjourney.service.DiscordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hncboy
 * @date 2023/5/19
 * Discord 业务接口实现类
 */
@Slf4j
@Service
public class DiscordServiceImpl implements DiscordService {

    @Resource
    private ObjectMapper objectMapper;

    private final String imagineParamsJson;
    private final String upscaleParamsJson;
    private final String variationParamsJson;
    private final String describeParamsJson;

    public DiscordServiceImpl() {
        this.imagineParamsJson = ResourceUtil.readUtf8Str("midjourney/imagine.json");
        this.upscaleParamsJson = ResourceUtil.readUtf8Str("midjourney/upscale.json");
        this.variationParamsJson = ResourceUtil.readUtf8Str("midjourney/variation.json");
        this.describeParamsJson = ResourceUtil.readUtf8Str("midjourney/describe.json");
    }

    @Override
    public Pair<Boolean, String> imagine(String prompt, MidjourneyProperties midjourneyProperties) {
        String requestBodyStr = imagineParamsJson
                .replace("$guild_id", midjourneyProperties.getGuildId())
                .replace("$channel_id", midjourneyProperties.getChannelId());

        JsonNode requestBodyNode = ObjectMapperUtil.readTree(requestBodyStr);
        // 转为 JsonNode 再修改防止 JSON 转义报错
        ((ObjectNode) requestBodyNode.get("data").get("options").get(0)).put("value", prompt);
        return executeRequest(midjourneyProperties, requestBodyNode.toString());
    }

    @Override
    public Pair<Boolean, String> upscale(String discordMessageId, int index, String discordMessageHash, MidjourneyProperties midjourneyProperties) {
        String requestBodyStr = upscaleParamsJson
                .replace("$guild_id", midjourneyProperties.getGuildId())
                .replace("$channel_id", midjourneyProperties.getChannelId())
                .replace("$message_id", discordMessageId)
                .replace("$index", String.valueOf(index))
                .replace("$message_hash", discordMessageHash);
        return executeRequest(midjourneyProperties, requestBodyStr);
    }

    @Override
    public Pair<Boolean, String> variation(String discordMessageId, int index, String discordMessageHash, MidjourneyProperties midjourneyProperties) {
        String requestBodyStr = variationParamsJson
                .replace("$guild_id", midjourneyProperties.getGuildId())
                .replace("$channel_id", midjourneyProperties.getChannelId())
                .replace("$message_id", discordMessageId)
                .replace("$index", String.valueOf(index))
                .replace("$message_hash", discordMessageHash);
        return executeRequest(midjourneyProperties, requestBodyStr);
    }

    @Override
    public Pair<Boolean, String> uploadImage(String fileName, MultipartFile multipartFile, MidjourneyProperties midjourneyProperties) {
        try {
            // 构建请求 JsonNode
            JsonNode reuqestJsonNode = objectMapper.createObjectNode().set("files",
                    objectMapper.createArrayNode().add(objectMapper.createObjectNode()
                            .put("filename", fileName)
                            .put("file_size", multipartFile.getSize())
                            .put("id", "0")));
            // 预请求要上传的图片
            ForestResponse<?> forestResponse = executeRequestAsResponse(midjourneyProperties, midjourneyProperties.getDiscordUploadUrl(), ObjectMapperUtil.toJson(reuqestJsonNode));
            if (forestResponse.isError()) {
                log.error("Midjourney describe 预处理图片失败，文件名：{}，响应消息： {}", fileName, forestResponse.getContent());
                return new Pair<>(false, "上传图片失败，请稍后重试");
            }
            // 解析响应内容
            JsonNode attachments = objectMapper.readTree(forestResponse.getContent()).get("attachments");
            if (attachments.size() == 0) {
                log.error("Midjourney describe 预处理图片响应 attachments 为空，文件名：{}，响应消息： {}", fileName, forestResponse.getContent(), forestResponse.getException());
                return new Pair<>(false, "上传图片失败，请稍后重试");

            }

            // 获取真实上传图片地址和文件名
            String uploadUrl = attachments.get(0).get("upload_url").asText();
            String uploadFilename = attachments.get(0).get("upload_filename").asText();

            // 构建真实上传图片请求
            ForestRequest<?> forestRequest = Forest.put(uploadUrl)
                    .setContentType(multipartFile.getContentType())
                    .addBody(multipartFile.getBytes())
                    .setUserAgent(midjourneyProperties.getUserAgent())
                    .addHeader(Header.CONTENT_LENGTH.name(), multipartFile.getSize());
            ForestRequestUtil.buildProxy(forestRequest);
            forestResponse = forestRequest.execute(ForestResponse.class);
            if (forestResponse.isError()) {
                log.error("Midjourney describe 真实上传图片失败，文件名：{}，响应消息： {}", fileName, forestResponse.getContent(), forestResponse.getException());
                return new Pair<>(false, "上传图片失败，请稍后重试");
            }

            return new Pair<>(true, uploadFilename);
        } catch (Exception e) {
            log.error("Midjourney describe 上传图片失败，文件名：{}", fileName, e);
            return new Pair<>(false, "上传图片失败，请稍后重试");
        }
    }

    @Override
    public Pair<Boolean, String> describe(String uploadFileName, MidjourneyProperties midjourneyProperties) {
        // 拆分文件名
        String fileName = CharSequenceUtil.subAfter(uploadFileName, "/", true);
        String requestBodyStr = describeParamsJson
                .replace("$guild_id", midjourneyProperties.getGuildId())
                .replace("$channel_id", midjourneyProperties.getChannelId())
                .replace("$file_name", fileName)
                .replace("$final_file_name", uploadFileName);
        return executeRequest(midjourneyProperties, requestBodyStr);
    }

    /**
     * 执行请求
     *
     * @param midjourneyProperties Midjourney 配置
     * @param discordUrl           请求地址
     * @param requestBodyStr       请求参数
     * @return 响应
     */
    private ForestResponse<?> executeRequestAsResponse(MidjourneyProperties midjourneyProperties, String discordUrl, String requestBodyStr) {
        // 构建请求
        ForestRequest<?> forestRequest = Forest.post(discordUrl)
                .contentTypeJson()
                .addHeader(Header.AUTHORIZATION.name(), midjourneyProperties.getUserToken())
                .setUserAgent(midjourneyProperties.getUserAgent())
                .addBody(requestBodyStr);

        // 设置代理
        ForestRequestUtil.buildProxy(forestRequest);

        // 发起请求
        return forestRequest.execute(ForestResponse.class);
    }

    /**
     * 执行请求
     *
     * @param midjourneyProperties Midjourney 配置
     * @param requestBodyStr       请求参数
     * @return 响应
     */
    private Pair<Boolean, String> executeRequest(MidjourneyProperties midjourneyProperties, String requestBodyStr) {
        ForestResponse<?> forestResponse = executeRequestAsResponse(midjourneyProperties, midjourneyProperties.getDiscordApiUrl(), requestBodyStr);
        if (forestResponse.isError()) {
            log.error("Midjourney 调用 Discord 接口失败，请求参数：{}，响应消息： {}", requestBodyStr, forestResponse.getContent(), forestResponse.getException());
            return new Pair<>(false, "调用接口发起请求失败，请稍后重试");
        }

        return new Pair<>(true, null);
    }
}
