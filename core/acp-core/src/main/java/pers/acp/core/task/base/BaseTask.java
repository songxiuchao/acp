package pers.acp.core.task.base;

import org.joda.time.DateTime;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.util.concurrent.Callable;

/**
 * 任务基类
 *
 * @author zb
 */
public abstract class BaseTask implements IBaseTask, Callable<Object> {

    /**
     * 日志对象
     */
    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 创建时间
     */
    private DateTime generateTime;

    /**
     * 提交执行时间
     */
    private DateTime submitTime = null;

    /**
     * 开始执行时间
     */
    private DateTime beginExecuteTime = null;

    /**
     * 执行完成时间
     */
    private DateTime finishTime = null;

    /**
     * 任务执行结果
     */
    private Object taskResult = null;

    /**
     * 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    private boolean needExecuteImmediate = false;

    /**
     * 任务是否处于正在执行状态
     */
    private volatile boolean isRunning = false;

    /**
     * 创建任务
     *
     * @param taskName 任务名称
     */
    public BaseTask(String taskName) {
        this.taskId = CommonUtils.getUuid();
        this.generateTime = CommonUtils.getNowDateTime();
        this.taskName = taskName;
    }

    /**
     * 创建任务
     *
     * @param taskName             任务名称
     * @param needExecuteImmediate 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    public BaseTask(String taskName, boolean needExecuteImmediate) {
        this.taskId = CommonUtils.getUuid();
        this.generateTime = CommonUtils.getNowDateTime();
        this.taskName = taskName;
        this.needExecuteImmediate = needExecuteImmediate;
    }

    @Override
    public Object call() {
        this.isRunning = true;
        this.beginExecuteTime = new DateTime();
        Object result = null;
        try {
            if (this.beforeExecuteFun()) {
                result = this.executeFun();
                this.setTaskResult(result);
                if (result != null) {
                    this.afterExecuteFun(result);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = null;
        }
        this.finishTime = CommonUtils.getNowDateTime();
        this.isRunning = false;
        this.setTaskResult(result);
        return result;
    }

    /**
     * 执行任务，返回执行结果
     *
     * @return 执行结果对象
     */
    public Object doExecute() {
        return call();
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public DateTime getGenerateTime() {
        return generateTime;
    }

    public DateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(DateTime submitTime) {
        this.submitTime = submitTime;
    }

    public DateTime getBeginExecuteTime() {
        return beginExecuteTime;
    }

    public DateTime getFinishTime() {
        return finishTime;
    }

    public Object getTaskResult() {
        return taskResult;
    }

    protected void setTaskResult(Object taskResult) {
        this.taskResult = taskResult;
    }

    /**
     * 是否需要立即执行，不使用工作线程
     *
     * @return false-等待执行策略；true-立即执行
     */
    public boolean isNeedExecuteImmediate() {
        return needExecuteImmediate;
    }

    public void setNeedExecuteImmediate(boolean needExecuteImmediate) {
        this.needExecuteImmediate = needExecuteImmediate;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
