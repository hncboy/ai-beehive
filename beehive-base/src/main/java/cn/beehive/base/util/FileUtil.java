package cn.beehive.base.util;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.hutool.core.util.BooleanUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author hncboy
 * @date 2023/5/20
 * 填写注释
 */
@Slf4j
public class FileUtil {

    /**
     * 下载网络图片到本地
     *
     * @param fileUrl  文件地址
     * @param savePath 保存地址
     */
    public static void downloadLocalFromUrl(String fileUrl, String savePath) {
        try {
            // 构建请求
            ForestRequest<?> forestRequest = Forest.get(fileUrl);
            // 判断是否需要代理
            if (BooleanUtil.toBoolean(SysParamCache.getValue(SysParamKeyEnum.ENABLE_PROXY))) {
                forestRequest.proxy(new ForestProxy(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_HOST), Integer.parseInt(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_PORT))));
            }
            // 发起请求
            ForestResponse<?> forestResponse = forestRequest.execute(ForestResponse.class);

            // 保存文件
            Files.copy(forestResponse.getInputStream(), Path.of(savePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("下载文件到本地失败，fileUrl：{}，savePath：{}", fileUrl, savePath, e);
        }
    }
}
