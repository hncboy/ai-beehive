package com.hncboy.beehive.cell.midjourney.util;

import cn.hutool.core.util.ObjectUtil;
import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.hncboy.beehive.base.util.FileUtil;
import com.hncboy.beehive.base.util.PictureUtil;
import com.hncboy.beehive.cell.midjourney.constant.MidjourneyConstant;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;

/**
 * @author hncboy
 * @date 2023/5/22
 * Midjourney 房间消息工具类
 */
@Slf4j
@UtilityClass
public class MjRoomMessageUtil {

    /**
     * 判断指定位置的 uv 是否使用
     * 末尾 4 位 0000，分别表示 U1 U2 U3 U4
     *
     * @param index  位置
     * @param action 动作
     * @return 是否使用
     */
    public boolean isUpscaleUse(int uUseBit, int index, MidjourneyMsgActionEnum action) {
        if (action == MidjourneyMsgActionEnum.UPSCALE) {
            return isBitSet(uUseBit, 4 - index);
        }
        throw new IllegalArgumentException("Mj 判断的 action 不合法");
    }

    /**
     * 设置指定位置的 upscale 使用
     *
     * @param index  位置
     * @param action 动作
     * @return 设置后的 uv 使用
     */
    public int setUpscaleUse(int uvUseBit, int index, MidjourneyMsgActionEnum action) {
        if (action == MidjourneyMsgActionEnum.UPSCALE) {
            return setBit(uvUseBit, 4 - index);
        }
        throw new IllegalArgumentException("Mj 设置的 action 不合法");
    }

    /**
     * 检查指定位置的位是否为 1
     *
     * @param number   要检查的数字
     * @param position 要检查的位的位置（从右向左数，最右边的位的位置为 0）
     * @return true，如果指定位置的位为 1；否则，返回 false
     */
    private boolean isBitSet(int number, int position) {
        int mask = 1 << position;
        return (number & mask) != 0;
    }

    /**
     * 将指定位置的位设置为 1
     *
     * @param number   要操作的数字
     * @param position 要设置的位的位置（从右向左数，最右边的位的位置为 0）
     * @return 操作后的结果
     */
    private static int setBit(int number, int position) {
        int mask = 1 << position;
        return number | mask;
    }

    /**
     * 下载压缩图
     *
     * @param originalImageName 原始图片名称
     * @param roomMjMsgId       房间消息 id
     * @return 缩略图名称
     */
    public String downloadCompressedImage(String originalImageName, Long roomMjMsgId) {
        try {
            String fileSavePathPrefix = FileUtil.getFileSavePathPrefix();

            // 如果是 webp，先转为 png，因为 webp 无法压缩，虽然 webp 已经很小了，但是为了加载速度还是压缩下
            String fileExtension = FileUtil.getFileExtension(originalImageName);
            if (ObjectUtil.equals(fileExtension, "webp")) {
                String pngFileName = originalImageName.replace("webp", "png");
                Boolean result = PictureUtil.webpToPng(fileSavePathPrefix + originalImageName, fileSavePathPrefix + pngFileName);
                if (!result) {
                    return "转换图片格式失败";
                }
                originalImageName = pngFileName;
            }

            // 原始文件
            File originalFile = new File(fileSavePathPrefix.concat(originalImageName));
            // 压缩图文件名
            String compressedFileName = MidjourneyConstant.COMPRESSED_FILE_PREFIX.concat(String.valueOf(roomMjMsgId)).concat(".png");
            // 压缩文件
            File compressFile = new File(fileSavePathPrefix + compressedFileName);

            // 压缩图片
            Thumbnails.of(originalFile)
                    .scale(0.1)
                    .toFile(compressFile);
            return compressedFileName;
        } catch (Exception e) {
            log.error("Midjourney 压缩图片失败，消息 id：{}", roomMjMsgId, e);
        }
        return "压缩图片失败";
    }
}
