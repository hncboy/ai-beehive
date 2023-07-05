package com.hncboy.beehive.base.util;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/20
 * 文件相关工具类
 */
@UtilityClass
@Slf4j
public class FileUtil {

    /**
     * 下载网络图片到本地
     *
     * @param fileUrl  文件地址
     * @param savePath 保存地址
     */
    public void downloadFromUrl(String fileUrl, String savePath) {
        try {
            // 构建完整路径
            savePath = getFileSavePathPrefix().concat(savePath);

            // 构建请求
            ForestRequest<?> forestRequest = Forest.get(fileUrl);
            ForestRequestUtil.buildProxy(forestRequest);
            // 发起请求
            ForestResponse<?> forestResponse = forestRequest.execute(ForestResponse.class);

            // 保存文件
            Files.copy(forestResponse.getInputStream(), Path.of(savePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("网络地址下载文件到本地失败，fileUrl：{}，savePath：{}", fileUrl, savePath, e);
        }
    }

    /**
     * 下载 MultipartFile 到本地
     *
     * @param multipartFile 文件
     * @param fileName      文件名
     */
    public void downloadFromMultipartFile(MultipartFile multipartFile, String fileName) {
        // 构建完整路径
        String savePath = getFileSavePathPrefix().concat(fileName);
        File targetFile = new File(savePath);
        try (InputStream inputStream = multipartFile.getInputStream();
             OutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("MultipartFile 下载文件到本地失败，savePath：{}", savePath, e);
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param filename 文件名
     * @return 后缀名
     */
    public String getFileExtension(String filename) {
        if (StrUtil.isEmpty(filename)) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(StrPool.DOT);
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return null;
    }

    /**
     * 获取文件保存路径前缀
     *
     * @return 前缀
     */
    public String getFileSavePathPrefix() {
        return SpringUtil.getApplicationContext().getEnvironment().getProperty("file-path.save-prefix");
    }

    /**
     * 获取文件路径访问前缀
     *
     * @return 前缀
     */
    public String getFilePathVisitPrefix() {
        return "/".concat(Objects.requireNonNull(SpringUtil.getApplicationContext().getEnvironment().getProperty("file-path.visit-prefix"))).concat("/");
    }
}
