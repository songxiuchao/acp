package pers.acp.springboot.interfaces;

/**
 * Create by zhangbin on 2017-10-28 0:56
 */
public interface ITimerTaskScheduler {

    int START = 1;

    int STOP = 0;

    /**
     * 定时器控制
     *
     * @param command ITimerTaskScheduler.START | ITimerTaskScheduler.STOP
     */
    void controlSchedule(int command) throws InterruptedException;

}
