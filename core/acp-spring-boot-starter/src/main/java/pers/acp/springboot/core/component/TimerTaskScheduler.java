package pers.acp.springboot.core.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.springboot.core.conf.ScheduleConfiguration;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.interfaces.ITimerTaskScheduler;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Create by zhangbin on 2017-10-27 22:42
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TimerTaskScheduler implements ITimerTaskScheduler {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ScheduleConfiguration scheduleConfiguration;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final Map<String, BaseSpringBootScheduledTask> baseSpringBootScheduledTaskMap;

    private final ConcurrentHashMap<String, BaseSpringBootScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

    @Autowired
    public TimerTaskScheduler(TaskSchedulingProperties properties, ScheduleConfiguration scheduleConfiguration, Map<String, BaseSpringBootScheduledTask> baseSpringBootScheduledTaskMap) {
        this.scheduleConfiguration = scheduleConfiguration;
        this.baseSpringBootScheduledTaskMap = baseSpringBootScheduledTaskMap;
        this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        this.threadPoolTaskScheduler.setPoolSize(properties.getPool().getSize());
        this.threadPoolTaskScheduler.setThreadNamePrefix(properties.getThreadNamePrefix());
        this.threadPoolTaskScheduler.initialize();
    }

    /**
     * 启动定时任务，此时服务变为“主”服务
     */
    private void startSchedule() throws InterruptedException {
        if (!scheduledTaskMap.isEmpty() || !futureMap.isEmpty()) {
            stopSchedule();
        }
        baseSpringBootScheduledTaskMap.forEach((key, scheduledTask) -> {
            Map<String, String> crons = scheduleConfiguration.getCrons();
            if (crons != null && !crons.isEmpty() && crons.containsKey(key) && !CommonTools.isNullStr(crons.get(key)) && !"none".equalsIgnoreCase(crons.get(key))) {
                String cron = crons.get(key);
                scheduledTaskMap.put(key, scheduledTask);
                ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(scheduledTask::executeScheduledTask, new CronTrigger(cron));
                if (future != null) {
                    futureMap.put(key, future);
                    log.info("启动定时任务：" + scheduledTask.getTaskName() + " 【" + cron + "】 【" + scheduledTask.getClass().getCanonicalName() + "】");
                }
            }
        });
    }

    /**
     * 停止定时任务，此时服务变为“从”服务
     */
    private void stopSchedule() throws InterruptedException {
        Iterator<Map.Entry<String, BaseSpringBootScheduledTask>> it = scheduledTaskMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BaseSpringBootScheduledTask> entry = it.next();
            String key = entry.getKey();
            BaseSpringBootScheduledTask scheduledTask = entry.getValue();
            ScheduledFuture<?> future = futureMap.remove(key);
            while (!scheduledTask.isWaiting()) {
                Thread.sleep(3000);
            }
            if (future != null) {
                future.cancel(true);
            }
            log.info("停止定时任务：" + scheduledTask.getTaskName() + " 【" + scheduledTask.getClass().getCanonicalName() + "】");
            it.remove();
        }
    }

    @Override
    public void controlSchedule(int command) throws InterruptedException {
        synchronized (this) {
            if (command == ITimerTaskScheduler.START) {
                startSchedule();
            } else if (command == ITimerTaskScheduler.STOP) {
                stopSchedule();
            }
        }
    }

}
