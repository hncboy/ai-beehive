package cn.beehive.base.resource.aip;

import cn.beehive.base.cache.SysParamCache;
import com.baidu.aip.contentcensor.AipContentCensor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/6
 * @see <href="https://console.bce.baidu.com/"/>
 * 百度 AI 配置
 */
@Slf4j
@Data
@Configuration
public class BaiduAipConfig implements InitializingBean {

    /**
     * appId
     */
    public String appId;

    /**
     * apiKey
     */
    public String apiKey;

    /**
     * secretKey
     */
    public String secretKey;

    private AipContentCensor aipContentCensor;

    @Override
    public void afterPropertiesSet() {
        List<String> paramKeys = new ArrayList<>();
        paramKeys.add(BaiduAipConstant.ENABLED);
        paramKeys.add(BaiduAipConstant.APP_ID);
        paramKeys.add(BaiduAipConstant.APP_KEY);
        paramKeys.add(BaiduAipConstant.SECRET_KEY);
        Map<String, String> sysParamMap = SysParamCache.multiGet(paramKeys);

        // 项目启动时初始化，如果要修改配置，需要重启项目
        this.appId = sysParamMap.get(BaiduAipConstant.APP_ID);
        this.apiKey = sysParamMap.get(BaiduAipConstant.APP_KEY);
        this.secretKey = sysParamMap.get(BaiduAipConstant.SECRET_KEY);
        this.aipContentCensor = new AipContentCensor(appId, apiKey, secretKey);

        log.info("百度 AI 配置初始化：{}", this);
    }

    /**
     * 百度 Aip 常量
     */
    public interface BaiduAipConstant {

        String PREFIX = "baidu-aip-";

        String ENABLED = PREFIX + "enabled";
        String APP_ID = PREFIX + "appId";
        String APP_KEY = PREFIX + "appKey";
        String SECRET_KEY = PREFIX + "secretKey";
    }
}
