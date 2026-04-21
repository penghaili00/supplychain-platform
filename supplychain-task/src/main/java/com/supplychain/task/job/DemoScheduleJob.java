package com.supplychain.task.job;

import com.supplychain.service.api.dubbo.DemoDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoScheduleJob {

    @DubboReference(check = false)
    private DemoDubboService demoDubboService;

    @Scheduled(cron = "${supplychain.task.demo-cron:0 0/5 * * * ?}")
    public void execute() {
        try {
            log.info("task invoke result: {}", demoDubboService.ping("supplychain-task"));
        } catch (Exception exception) {
            log.warn("task invoke provider failed", exception);
        }
    }
}
