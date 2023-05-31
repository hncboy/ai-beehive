package cn.beehive.base.util;

import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.handler.response.R;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/5/31
 * ResponseBodyEmitter 工具类
 */
public class ResponseBodyEmitterUtil {

    /**
     * 发送 emitter 消息
     *
     * @param emitter 响应流
     * @param object  消息
     */
    public static void send(ResponseBodyEmitter emitter, Object object) {
        try {
            if (object instanceof R) {
                emitter.send(object);
            } else {
                emitter.send(R.data(object));
            }
        } catch (Exception e) {
            throw new ServiceException(StrUtil.format("消息发送异常，异常信息：{}", e.getMessage()));
        }
    }

    /**
     * 发送 emitter 消息并且结束
     *
     * @param emitter 响应流
     * @param object  消息
     */
    public static void sendWithComplete(ResponseBodyEmitter emitter, Object object) {
        try {
            send(emitter, object);
        } finally {
            emitter.complete();
        }
    }
}
