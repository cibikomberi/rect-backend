package com.rect.iot.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamicTaskScheduler {

    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks;

    // TODO: need to load all on startup
    public DynamicTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.initialize();
        this.taskScheduler = scheduler;
        this.scheduledTasks = new ConcurrentHashMap<>();
    }

    public String scheduleEvent(String taskId, Runnable task, String timeString) {
        LocalTime triggerTime = LocalTime.parse(timeString);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTriggerDateTime = now.toLocalDate().atTime(triggerTime);

        if (nextTriggerDateTime.isBefore(now)) {
            nextTriggerDateTime = nextTriggerDateTime.plusDays(1);
        }

        Date nextTriggerDate = Date.from(nextTriggerDateTime.atZone(ZoneId.systemDefault()).toInstant());

        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
            task.run();
            scheduleEvent(taskId, task, timeString);
        }, nextTriggerDate.toInstant());

        scheduledTasks.put(taskId, scheduledFuture);
        return taskId;
    }

    public void cancelEvent(String taskId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(taskId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            log.info("Task '%s' has been canceled\n", taskId);
        } else {
            log.info("Task '%s' not found\n", taskId);
        }
    }

}
