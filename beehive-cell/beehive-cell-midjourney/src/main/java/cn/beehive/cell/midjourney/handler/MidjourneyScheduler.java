package cn.beehive.cell.midjourney.handler;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/5/20
 * Midjourney 定时任务
 */
@Slf4j
@Component
@DependsOn("discordStarter")
public class MidjourneyScheduler {

    @Resource
    private MjTaskQueueHandler mjTaskQueueHandler;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void pullTask() {
        // 因为意外可能导致一直排队，通过定时任务避免这种情况
        log.info("Midjourney 定时任务开始");
        mjTaskQueueHandler.checkAndPullTask();
    }
}
