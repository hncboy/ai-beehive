package cn.beehive.base.resource.aip;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.beehive.base.util.ObjectMapperUtil;
import lombok.experimental.UtilityClass;

/**
 * @author hncboy
 * @date 2023/6/26
 * 百度 AIP 工具类
 */
@UtilityClass
public class BaiduAipUtil {

    /**
     * 获取 BaiduAipProperties
     *
     * @return BaiduAipProperties
     */
    public BaiduAipProperties getBaiduAipProperties() {
        String baiduAipConfigStr = SysParamCache.get(SysParamKeyEnum.BAIDU_AIP);
        return ObjectMapperUtil.fromJson(baiduAipConfigStr, BaiduAipProperties.class);
    }
}
