package pers.acp.springboot.base;

import pers.acp.core.task.timer.basetask.BaseTimerTask;

/**
 * spring boot ScheduledTask
 * author zb
 */
public abstract class BaseSpringBootScheduledTask extends BaseTimerTask {

    /**
     * 构造函数
     */
    public BaseSpringBootScheduledTask() {
        super("");
    }

    /**
     * spring boot ScheduledTask 入口，注解需在此方法上
     */
    public void executeScheduledTask() {
        this.doExecute();
    }

    /**
     * 改任务是否处于等待状态
     *
     * @return true|false
     */
    public boolean isWaiting() {
        return !this.isRunning();
    }

}
