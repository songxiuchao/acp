package pers.acp.core.task.threadpool.basetask;

import pers.acp.core.task.base.BaseTask;

/**
 * 线程池任务基类，其他任务必须继承此类
 *
 * @author zhangbin
 */
public abstract class BaseThreadTask extends BaseTask {

    /**
     * 创建任务
     *
     * @param taskName 任务名称
     */
    protected BaseThreadTask(String taskName) {
        super(taskName);
    }

    /**
     * 创建任务
     *
     * @param taskName             任务名称
     * @param needExecuteImmediate 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    protected BaseThreadTask(String taskName, boolean needExecuteImmediate) {
        super(taskName, needExecuteImmediate);
    }

}