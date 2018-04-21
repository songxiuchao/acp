package pers.acp.springboot.common.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.springboot.common.conf.ScheduleConfiguration;
import pers.acp.springboot.common.interfaces.ITimerTaskScheduler;
import pers.acp.springboot.core.tools.SpringBeanFactory;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Create by zhangbin on 2017-10-27 22:42
 */
@Component
@Scope("singleton")
public class TimerTaskScheduler implements ITimerTaskScheduler {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ScheduleConfiguration scheduleConfiguration;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final ConcurrentHashMap<String, BaseSpringBootScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

    @Autowired
    public TimerTaskScheduler(ScheduleConfiguration scheduleConfiguration, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.scheduleConfiguration = scheduleConfiguration;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    /**
     * 启动定时任务，此时服务变为“主”服务
     */
    private void startSchedule() throws InterruptedException {
        if (!scheduledTaskMap.isEmpty() || !futureMap.isEmpty()) {
            stopSchedule();
        }
        Map<String, BaseSpringBootScheduledTask> scheduledTaskMap_tmp = SpringBeanFactory.getApplicationContext().getBeansOfType(BaseSpringBootScheduledTask.class);
        scheduledTaskMap_tmp.forEach((key, scheduledTask) -> {
            Map<String, String> crons = scheduleConfiguration.getCrons();
            if (crons != null && !crons.isEmpty() && crons.containsKey(key) && !CommonTools.isNullStr(crons.get(key)) && !"none".equalsIgnoreCase(crons.get(key))) {
                String cron = crons.get(key);
                scheduledTaskMap.put(key, scheduledTask);
                ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(scheduledTask::executeScheduledTask, new CronTrigger(cron));
                futureMap.put(key, future);
                log.info("启动定时任务：" + scheduledTask.getTaskName() + " 【" + cron + "】 【" + scheduledTask.getClass().getCanonicalName() + "】");
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
            if (command == START) {
                startSchedule();
            } else if (command == STOP) {
                stopSchedule();
            }
        }
    }

}
