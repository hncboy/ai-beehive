package cn.beehive.cell.midjourney.service.impl;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.beehive.cell.midjourney.service.DiscordService;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.http.Header;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/5/19
 * Discord 业务接口实现类
 */
@Service
public class DiscordServiceImpl implements DiscordService {

    @Resource
    private MidjourneyConfig midjourneyConfig;

    /**
     * 请求地址
     */
    private static final String DISCORD_API_URL = "https://discord.com/api/v9/interactions";

    private final String imagineParamsJson;
    private final String upscaleParamsJson;
    private final String variationParamsJson;

    public DiscordServiceImpl() {
        this.imagineParamsJson = ResourceUtil.readUtf8Str("midjourney/imagine.json");
        this.upscaleParamsJson = ResourceUtil.readUtf8Str("midjourney/upscale.json");
        this.variationParamsJson = ResourceUtil.readUtf8Str("midjourney/variation.json");
    }

    @Override
    public Pair<Boolean, String> imagine(String prompt) {
        String requestBodyStr = imagineParamsJson
                .replace("$guild_id", midjourneyConfig.getGuildId())
                .replace("$channel_id", midjourneyConfig.getChannelId())
                .replace("$prompt", prompt);
        return executeRequest(requestBodyStr);
    }

    @Override
    public Pair<Boolean, String> upscale(String discordMessageId, int index, String discordMessageHash) {
        String requestBodyStr = upscaleParamsJson
                .replace("$guild_id", midjourneyConfig.getGuildId())
                .replace("$channel_id", midjourneyConfig.getChannelId())
                .replace("$message_id", discordMessageId)
                .replace("$index", String.valueOf(index))
                .replace("$message_hash", discordMessageHash);
        return executeRequest(requestBodyStr);
    }

    @Override
    public Pair<Boolean, String> variation(String discordMessageId, int index, String discordMessageHash) {
        String requestBodyStr = variationParamsJson
                .replace("$guild_id", midjourneyConfig.getGuildId())
                .replace("$channel_id", midjourneyConfig.getChannelId())
                .replace("$message_id", discordMessageId)
                .replace("$index", String.valueOf(index))
                .replace("$message_hash", discordMessageHash);
        return executeRequest(requestBodyStr);
    }

    /**
     * 执行请求
     *
     * @param requestBodyStr 请求参数
     * @return 响应
     */
    private Pair<Boolean, String> executeRequest(String requestBodyStr) {
        // 构建请求
        ForestRequest<?> forestRequest = Forest.post(DISCORD_API_URL)
                .contentTypeJson()
                .addHeader(Header.AUTHORIZATION.name(), midjourneyConfig.getUserToken())
                .setUserAgent(midjourneyConfig.getUserAgent())
                .addBody(requestBodyStr);

        // 判断是否需要代理
        if (BooleanUtil.toBoolean(SysParamCache.getValue(SysParamKeyEnum.ENABLE_PROXY))) {
            forestRequest.proxy(new ForestProxy(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_HOST), Integer.parseInt(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_PORT))));
        }

        // 发起请求
        ForestResponse<?> forestResponse = forestRequest.execute(ForestResponse.class);
        return Pair.of(forestResponse.isSuccess(), forestResponse.getContent());
    }
}
