package cn.beehive.base.util;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.hutool.core.util.BooleanUtil;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author hncboy
 * @date 2023/5/24
 * ForestRequestUtil
 */
public class ForestRequestUtil {

    /**
     * 构建代理
     *
     * @param forestRequest 请求
     */
    public static void buildProxy(ForestRequest<?> forestRequest) {
        if (BooleanUtil.toBoolean(SysParamCache.getValue(SysParamKeyEnum.ENABLE_PROXY))) {
            forestRequest.proxy(new ForestProxy(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_HOST), Integer.parseInt(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_PORT))));
        }
    }
}
