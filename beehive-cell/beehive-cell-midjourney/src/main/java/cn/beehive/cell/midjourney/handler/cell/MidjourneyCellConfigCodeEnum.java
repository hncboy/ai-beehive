package cn.beehive.cell.midjourney.handler.cell;

import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;

/**
 * @author hncboy
 * @date 2023/6/4
 * Midjourney Cell 配置项枚举
 */
public enum MidjourneyCellConfigCodeEnum implements ICellConfigCodeEnum {

    /**
     * 服务器 id
     */
    GUILD_ID {
        @Override
        public String getCode() {
            return "guild_id";
        }
    },

    /**
     * 频道 id
     */
    CHANNEL_ID {
        @Override
        public String getCode() {
            return "channel_id";
        }
    },

    /**
     * 用户 token
     */
    USER_TOKEN {
        @Override
        public String getCode() {
            return "user_token";
        }
    },

    /**
     * 机器人 token
     */
    BOT_TOKEN {
        @Override
        public String getCode() {
            return "bot_token";
        }
    },

    /**
     * Midjourney 机器人的名称，需要一致
     */
    MJ_BOT_NAME {
        @Override
        public String getCode() {
            return "mj_bot_name";
        }
    },

    /**
     * 调用 discord 接口时的 user-agent
     */
    USER_AGENT {
        @Override
        public String getCode() {
            return "user_agent";
        }
    },

    /**
     * 图片存储路径
     */
    IMAGE_LOCATION {
        @Override
        public String getCode() {
            return "image_location";
        }
    },

    /**
     * 等待队列最大长度
     */
    MAX_WAIT_QUEUE_SIZE {
        @Override
        public String getCode() {
            return "max_wait_queue_size";
        }
    },

    /**
     * 执行队列最大长度
     */
    MAX_EXECUTE_QUEUE_SIZE {
        @Override
        public String getCode() {
            return "max_execute_queue_size";
        }
    },

    /**
     * 最大文件大小，用于 descibe 上传图片，单位字节
     */
    MAX_FILE_SIZE {
        @Override
        public String getCode() {
            return "max_file_size";
        }
    }
}
