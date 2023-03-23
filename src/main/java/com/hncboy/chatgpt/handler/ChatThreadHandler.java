package com.hncboy.chatgpt.handler;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.exception.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hncboy
 * @date 2023/3/23 20:47
 * 消息线程处理器
 */
public class ChatThreadHandler {

    /**
     * 同时请求的数量
     */
    private static final int THREAD_POOL_SIZE = 1;

    /**
     * 拒绝策略
     */
    private static final RejectedExecutionHandler REJECTED_HANDLER = (r, executor) -> {
        throw new ServiceException(StrUtil.format("每次只能有{}条请求，请等等前面的同学", THREAD_POOL_SIZE));
    };

    /**
     * 线程工厂
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger threadCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "chat-thread-" + threadCount.getAndIncrement());
        }
    };

    /**
     * 定义的线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), THREAD_FACTORY, REJECTED_HANDLER);

    /**
     * 执行聊天处理
     *
     * @param runnable 线程
     * @param emitter  emitter
     */
    public static void executeChatProcess(Runnable runnable, ResponseBodyEmitter emitter) {
        EXECUTOR_SERVICE.submit(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                runnable.run();
                emitter.onCompletion(latch::countDown);
                emitter.onError(throwable -> latch.countDown());
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
    }
}
