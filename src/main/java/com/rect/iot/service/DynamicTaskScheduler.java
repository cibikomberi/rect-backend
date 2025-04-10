package com.rect.iot.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicTaskScheduler {
     private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks;

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
            System.out.printf("Task '%s' has been canceled\n", taskId);
        } else {
            System.out.printf("Task '%s' not found\n", taskId);
        }
    }

}
