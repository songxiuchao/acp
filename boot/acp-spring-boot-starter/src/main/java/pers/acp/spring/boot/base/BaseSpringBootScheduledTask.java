package pers.acp.spring.boot.base;

import pers.acp.core.task.timer.basetask.BaseTimerTask;

/**
 * 定时任务基类
 *
 * @author zhangbin by 2018-1-31 13:04
 * @since JDK 11
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
