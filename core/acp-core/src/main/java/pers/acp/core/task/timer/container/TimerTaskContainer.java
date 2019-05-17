package pers.acp.core.task.timer.container;

import org.joda.time.DateTime;
import pers.acp.core.exceptions.TimerException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.timer.basetask.BaseTimerTask;
import pers.acp.core.task.timer.ruletype.CircleType;
import pers.acp.core.task.timer.ruletype.ExecuteType;
import pers.acp.core.tools.CommonUtils;

/**
 * 定时器
 *
 * @author zhangbin
 */
public final class TimerTaskContainer implements Runnable {

    /**
     * 日志对象
     */
    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 执行周期
     */
    private CircleType circleType;

    /**
     * 执行时间点规则
     */
    private String rules;

    /**
     * 执行类型
     */
    private ExecuteType executeType;

    /**
     * 执行任务
     */
    private BaseTimerTask task;

    /**
     * 是否需要启动时立即执行
     */
    private boolean needExecuteImmediate;

    /**
     * 上次执行时间
     */
    private DateTime lastExecuteDateTime;

    /**
     * 定时任务容器构造函数
     *
     * @param task        任务
     * @param circleType  执行周期类型
     * @param rules       执行规则
     * @param executeType 执行类型
     */
    public TimerTaskContainer(BaseTimerTask task, CircleType circleType, String rules, ExecuteType executeType) {
        this.lastExecuteDateTime = CommonUtils.getNowDateTime();
        this.circleType = circleType;
        this.rules = rules;
        this.executeType = executeType;
        this.task = task;
        this.needExecuteImmediate = this.task.isNeedExecuteImmediate();
    }

    /**
     * 容器内是否存在任务
     *
     * @return 是否存在
     */
    public boolean isExistTask() {
        return this.task != null;
    }

    /**
     * 获取容器内任务名称
     *
     * @return 任务名称
     */
    public String getTaskName() {
        if (isExistTask()) {
            return this.task.getTaskName();
        } else {
            return null;
        }
    }

    /**
     * 是否需要启动时立即执行
     *
     * @return 是否立即执行
     */
    public boolean isNeedExecuteImmediate() {
        return needExecuteImmediate;
    }

    /**
     * 立即执行任务
     */
    public void immediateRun() {
        this.task.doExecute();
    }

    @Override
    public void run() {
        try {
            if (isExecute()) {
                doExecute();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 执行前函数
     */
    private void beforeExecute() {
        this.lastExecuteDateTime = CommonUtils.getNowDateTime();
    }

    /**
     * 执行任务
     */
    private void doExecute() throws TimerException {
        if (this.task != null) {
            log.info("begin TimerTask,taskName:["
                    + this.task.getTaskName()
                    + "] className:["
                    + this.task.getClass().getCanonicalName()
                    + "] creatTime:"
                    + this.task.getGenerateTime().toString(Calculation.DATETIME_FORMAT)
                    + " submitTime:"
                    + this.task.getSubmitTime().toString(Calculation.DATETIME_FORMAT));
            this.beforeExecute();
            Object result = this.task.doExecute();
            if (result == null) {
                log.error("timerTask [" + this.task.getTaskName() + "] execute failed...");
            }
        } else {
            throw new TimerException("timerTask is null");
        }
    }

    /**
     * 判断当前时间是否符合执行条件
     *
     * @return 结果对象
     */
    private boolean isExecute() {
        boolean isExecute;
        DateTime now = CommonUtils.getNowDateTime();
        try {
            boolean flag;
            switch (this.executeType) {
                case WeekDay:
                    flag = Calculation.isWeekDay(now);
                    break;
                case Weekend:
                    flag = Calculation.isWeekend(now);
                    break;
                case All:
                    flag = true;
                    break;
                default:
                    flag = false;
                    break;
            }
            switch (this.circleType) {
                case Time:
                    isExecute = true;
                    break;
                case Day:
                    boolean validate = Calculation.validateDay(now, this.lastExecuteDateTime, this.rules);
                    isExecute = flag && validate;
                    break;
                case Week:
                    isExecute = Calculation.validateWeek(now, this.lastExecuteDateTime, this.rules);
                    break;
                case Month:
                    isExecute = Calculation.validateMonth(now, this.lastExecuteDateTime, this.rules);
                    break;
                case Quarter:
                    isExecute = Calculation.validateQuarter(now, this.lastExecuteDateTime, this.rules);
                    break;
                case Year:
                    isExecute = Calculation.validateYear(now, this.lastExecuteDateTime, this.rules);
                    break;
                default:
                    log.error("circleType is not support:circleType=" + this.circleType.getName());
                    throw new TimerException("circleType is not support:circleType=" + this.circleType.getName());
            }
            return isExecute;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
