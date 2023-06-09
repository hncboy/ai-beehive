package cn.beehive.cell.midjourney.handler.listener;

import cn.beehive.base.config.ProxyConfig;
import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.cell.core.hander.CellHandler;
import cn.beehive.cell.midjourney.handler.cell.MidjourneyProperties;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/18
 * Discord 启动类
 */
@Slf4j
@DependsOn("midjourneyProperties")
@Component("discordStarter")
public class DiscordStarter implements InitializingBean {

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private DiscordMessageListener discordMessageListener;

    @Resource
    private MidjourneyProperties midjourneyProperties;

    @Override
    public void afterPropertiesSet() {
        CellDO cellDO = CellHandler.getCell(CellCodeEnum.MIDJOURNEY);
        if (Objects.isNull(cellDO) || cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            return;
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(midjourneyProperties.getBotToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(this.discordMessageListener);
        log.info("Midjourney 开始启动");

        // 判断是否需要代理
        if (!proxyConfig.getEnabled()) {
            return;
        }
        // TODO 有时间可以尝试改成 userToken 试下效果
        // 解决报错：java.net.SocketTimeoutException: Connect timed out
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        okhttpbuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfig.getHttpHost(), proxyConfig.getHttpPort())));
        builder.setHttpClientBuilder(okhttpbuilder);

        // 解决报错：com.neovisionaries.ws.client.WebSocketException: Failed to connect to 'gateway.discord.gg:443': Connect timed out
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        ProxySettings proxySettings = webSocketFactory.getProxySettings();
        proxySettings.setHost(proxyConfig.getHttpHost());
        proxySettings.setPort(proxyConfig.getHttpPort());
        builder.setWebsocketFactory(webSocketFactory);

        builder.build();
    }
}
