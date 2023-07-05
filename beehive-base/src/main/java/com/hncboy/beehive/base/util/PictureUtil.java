package com.hncboy.beehive.base.util;

import com.luciad.imageio.webp.WebPReadParam;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author hncboy
 * @date 2023/7/5
 * 图片工具
 */
@Slf4j
@UtilityClass
public class PictureUtil {

    /**
     * webp 转为 png
     *
     * @param webpFilePath webp 图片路径
     * @param pngFilePath  png 图片路径
     * @return 成功或失败
     */
    public Boolean webpToPng(String webpFilePath, String pngFilePath) {
        try {
            File webpFile = new File(webpFilePath);
            // 获取 WebP 图像读取器实例
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();

            // 配置解码参数
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);

            // 配置 ImageReader 的输入
            reader.setInput(new FileImageInputStream(webpFile));

            // 解码图像
            BufferedImage image = reader.read(0, readParam);

            File webpToPngFile = new File(pngFilePath);

            // 将图像保存为 PNG 文件
            ImageIO.write(image, "png", webpToPngFile);

            return true;
        } catch (Exception e) {
            log.error("webp 转存为 png 失败，webFilePath：{}，pngFilePath：{}", webpFilePath, pngFilePath, e);
        }

        return false;
    }
}
