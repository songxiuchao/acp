package pers.acp.spring.boot.base;

import pers.acp.core.task.BaseAsyncTask;

/**
 * 定时任务基类
 *
 * @author zhangbin by 2018-1-31 13:04
 * @since JDK 11
 */
public abstract class BaseSpringBootScheduledAsyncTask extends BaseAsyncTask {

    /**
     * 构造函数
     */
    public BaseSpringBootScheduledAsyncTask() {
        super("", false);
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
