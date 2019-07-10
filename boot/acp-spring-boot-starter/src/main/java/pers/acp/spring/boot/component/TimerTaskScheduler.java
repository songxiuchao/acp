package pers.acp.spring.boot.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask;
import pers.acp.spring.boot.conf.ScheduleConfiguration;
import pers.acp.spring.boot.interfaces.ITimerTaskScheduler;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务处理器
 *
 * @author zhangbin by 2018-1-20 21:24
 * @since JDK 11
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TimerTaskScheduler implements ITimerTaskScheduler {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ScheduleConfiguration scheduleConfiguration;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final Map<String, BaseSpringBootScheduledAsyncTask> baseSpringBootScheduledTaskMap;

    private final ConcurrentHashMap<String, BaseSpringBootScheduledAsyncTask> scheduledTaskMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

    @Autowired
    public TimerTaskScheduler(TaskSchedulingProperties properties, ScheduleConfiguration scheduleConfiguration, Map<String, BaseSpringBootScheduledAsyncTask> baseSpringBootScheduledTaskMap) {
        this.scheduleConfiguration = scheduleConfiguration;
        this.baseSpringBootScheduledTaskMap = baseSpringBootScheduledTaskMap;
        this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        this.threadPoolTaskScheduler.setPoolSize(properties.getPool().getSize());
        this.threadPoolTaskScheduler.setThreadNamePrefix(properties.getThreadNamePrefix());
        this.threadPoolTaskScheduler.initialize();
    }

    /**
     * 启动定时任务
     */
    private void startSchedule() throws InterruptedException {
        if (!scheduledTaskMap.isEmpty() || !futureMap.isEmpty()) {
            stopSchedule();
        }
        baseSpringBootScheduledTaskMap.forEach((key, scheduledTask) -> {
            Map<String, String> cronMap = scheduleConfiguration.getCrons();
            if (cronMap != null && !cronMap.isEmpty() && cronMap.containsKey(key) && !CommonTools.isNullStr(cronMap.get(key)) && !"none".equalsIgnoreCase(cronMap.get(key))) {
                String cron = cronMap.get(key);
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
     * 停止定时任务
     */
    private void stopSchedule() throws InterruptedException {
        Iterator<Map.Entry<String, BaseSpringBootScheduledAsyncTask>> it = scheduledTaskMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BaseSpringBootScheduledAsyncTask> entry = it.next();
            String key = entry.getKey();
            BaseSpringBootScheduledAsyncTask scheduledTask = entry.getValue();
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

    /**
     * 定时任务控制
     *
     * @param command ITimerTaskScheduler.START | ITimerTaskScheduler.STOP
     * @throws InterruptedException 异常
     */
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
