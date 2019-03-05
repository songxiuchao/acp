package pers.acp.core.task.timer.container;

import pers.acp.core.exceptions.TimerException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.timer.basetask.BaseTimerTask;
import pers.acp.core.task.timer.ruletype.CircleType;
import pers.acp.core.task.timer.ruletype.ExcuteType;

import java.util.Date;

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
    private ExcuteType excuteType;

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
    private Date lastExcuteDateTime;

    /**
     * 定时任务容器构造函数
     *
     * @param task       任务
     * @param circleType 执行周期类型
     * @param rules      执行规则
     * @param excuteType 执行类型
     */
    public TimerTaskContainer(BaseTimerTask task, CircleType circleType, String rules, ExcuteType excuteType) {
        this.lastExcuteDateTime = new Date();
        this.circleType = circleType;
        this.rules = rules;
        this.excuteType = excuteType;
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
        this.task.doExcute();
    }

    @Override
    public void run() {
        try {
            if (isExcute()) {
                doExcrute();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 执行前函数
     */
    private void beforeExcute() {
        this.lastExcuteDateTime = new Date();
    }

    /**
     * 执行任务
     */
    private void doExcrute() throws TimerException {
        if (this.task != null) {
            Calculation calculation = new Calculation();
            log.info("begin TimerTask,taskname:["
                    + this.task.getTaskName()
                    + "] classname:["
                    + this.task.getClass().getCanonicalName()
                    + "] creattime:"
                    + calculation.DATETIME_FORMAT.format(this.task
                    .getGenerateTime())
                    + " submittime:"
                    + calculation.DATETIME_FORMAT.format(this.task
                    .getSubmitTime()));
            this.beforeExcute();
            Object result = this.task.doExcute();
            if (result == null) {
                log.error("timertask[" + this.task.getTaskName() + "]excute failed...");
            }
        } else {
            throw new TimerException("timertask is null");
        }
    }

    /**
     * 判断当前时间是否符合执行条件
     *
     * @return 结果对象
     */
    private boolean isExcute() {
        boolean isexcute;
        Calculation calculation = new Calculation();
        Date now = new Date();
        try {
            boolean flag;
            switch (this.excuteType) {
                case WeekDay:
                    flag = calculation.isWeekDay(now);
                    break;
                case Weekend:
                    flag = calculation.isWeekend(now);
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
                    isexcute = true;
                    break;
                case Day:
                    boolean validate = calculation.validateDay(now, this.lastExcuteDateTime, this.rules);
                    isexcute = flag && validate;
                    break;
                case Week:
                    isexcute = calculation.validateWeek(now, this.lastExcuteDateTime, this.rules);
                    break;
                case Month:
                    isexcute = calculation.validateMonth(now, this.lastExcuteDateTime, this.rules);
                    break;
                case Quarter:
                    isexcute = calculation.validateQuarter(now, this.lastExcuteDateTime, this.rules);
                    break;
                case Year:
                    isexcute = calculation.validateYear(now, this.lastExcuteDateTime, this.rules);
                    break;
                default:
                    log.error("circleType is not support:circleType=" + this.circleType.getName());
                    throw new TimerException("circleType is not support:circleType=" + this.circleType.getName());
            }
            return isexcute;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
