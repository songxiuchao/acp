package pers.acp.core.task.base;

/**
 * 任务接口
 *
 * @author zhangbin
 */
public interface IBaseTask {

    /**
     * 任务执行前判断函数
     *
     * @return true-任务开始执行，false-任务拒绝执行
     */
    boolean beforeExecuteFun();

    /**
     * 执行任务函数
     *
     * @return Object-执行成功并进入afterExcute函数，null-执行失败不进入afterExcute函数
     */
    Object executeFun();

    /**
     * 任务执行后函数
     */
    void afterExecuteFun(Object result);
}
