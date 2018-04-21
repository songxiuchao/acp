package pers.acp.core.task.timer;

import pers.acp.core.exceptions.TimerException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.timer.basetask.BaseTimerTask;
import pers.acp.core.task.timer.container.Calculation;
import pers.acp.core.task.timer.container.TimerTaskContainer;
import pers.acp.core.task.timer.ruletype.CircleType;
import pers.acp.core.task.timer.ruletype.ExcuteType;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时器驱动
 *
 * @author zhangbin
 */
public class TimerDriver extends ScheduledThreadPoolExecutor {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    /**
     * 执行周期:Time, Day, Week, Month, Quarter, Year
     */
    private CircleType circleType = CircleType.Day;

    /**
     * 执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）, 日-时间（HH:MI:SS）,
     * 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     * 季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     */
    private String rules = "00:00:00";

    /**
     * 执行类型:WeekDay, Weekend, All
     */
    private ExcuteType excuteType = ExcuteType.All;

    /**
     * 任务容器
     */
    private TimerTaskContainer container = null;

    /**
     * 构造函数（无定时任务,周期默认“日”,规则默认“00:00:00”,执行类型默认“全部”）
     *
     * @param threadNumber 工作线程数
     */
    public TimerDriver(int threadNumber) {
        super(threadNumber);
    }

    /**
     * 构造函数
     *
     * @param threadNumber 工作线程数
     * @param circleType   执行周期:Time, Day, Week, Month, Quarter, Year
     * @param rules        执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）,
     *                     日-时间（HH:MI:SS）, 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     *                     季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     */
    public TimerDriver(int threadNumber, CircleType circleType, String rules) {
        this(threadNumber);
        this.circleType = circleType;
        this.rules = rules;
    }

    /**
     * 构造函数（周期默认“日”,规则默认“00:00:00”）
     *
     * @param threadNumber 工作线程数
     * @param excuteType   执行类型:WeekDay, Weekend, All
     */
    public TimerDriver(int threadNumber, ExcuteType excuteType) {
        this(threadNumber);
        this.excuteType = excuteType;
    }

    /**
     * 构造函数
     *
     * @param threadNumber 工作线程数
     * @param circleType   执行周期:Time, Day, Week, Month, Quarter, Year
     * @param rules        执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）,
     *                     日-时间（HH:MI:SS）, 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     *                     季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     * @param excuteType   执行类型:WeekDay, Weekend, All
     */
    public TimerDriver(int threadNumber, CircleType circleType, String rules, ExcuteType excuteType) {
        this(threadNumber, excuteType);
        this.circleType = circleType;
        this.rules = rules;
    }

    /**
     * 设置定时任务
     *
     * @param task 任务对象
     */
    public TimerTaskContainer setTimerTask(BaseTimerTask task) {
        if (this.container != null) {
            log.error("a TimerDriver can only receive one task");
            return null;
        }
        task.setSubmitTime(new Date());
        this.container = new TimerTaskContainer(task, this.circleType, this.rules, this.excuteType);
        return this.container;
    }

    /**
     * 获取定时器信息
     */
    public String getTimerInfo() {
        String info = "\ncircleType:" + this.circleType.getName() + "\nrules:" + this.rules + "\nexcuteType:" + this.excuteType.getName();
        if (this.container != null) {
            info += "\ntimertask[" + this.container.getTaskName() + "]\nisNeedExecuteImmediate=" + (this.container.isNeedExecuteImmediate() ? "true" : "false");
        }
        return info;
    }

    /**
     * 启动定时器
     */
    public void startTimer(BaseTimerTask task) {
        TimerTaskContainer taskContainer = this.setTimerTask(task);
        if (taskContainer != null) {
            runTimerTask();
        }
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        this.shutdown();
        log.info("stop timerDriver!");
    }

    /**
     * 启动定时器,执行定时任务
     */
    private void runTimerTask() {
        try {
            if (this.container != null && this.container.isExistTask()) {
                if (this.container.isNeedExecuteImmediate()) {
                    this.container.immediateRun();
                }
                long[] param = new Calculation().getTimerParam(this.circleType, this.rules);
                this.scheduleAtFixedRate(this.container, param[0], param[1], TimeUnit.MILLISECONDS);
                log.info("start timertask successfull:" + getTimerInfo());
            } else {
                throw new TimerException("timertask is null");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.stopTimer();
        }
    }

}
