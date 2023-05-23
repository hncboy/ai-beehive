package cn.beehive.cell.midjourney.util;

import cn.beehive.base.enums.MjMsgActionEnum;

/**
 * @author hncboy
 * @date 2023/5/22
 * Midjourney 房间消息工具类
 */
public class MjRoomMessageUtil {

    /**
     * 判断指定位置的 uv 是否使用
     * 末尾 4 位 0000，分别表示 U1 U2 U3 U4
     *
     * @param index  位置
     * @param action 动作
     * @return 是否使用
     */
    public static boolean isUpscaleUse(int uUseBit, int index, MjMsgActionEnum action) {
        if (action == MjMsgActionEnum.UPSCALE) {
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
    public static int setUpscaleUse(int uvUseBit, int index, MjMsgActionEnum action) {
        if (action == MjMsgActionEnum.UPSCALE) {
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
    private static boolean isBitSet(int number, int position) {
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
}
