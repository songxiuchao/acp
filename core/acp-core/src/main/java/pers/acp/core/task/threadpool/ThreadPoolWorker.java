package pers.acp.core.task.threadpool;

import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

/**
 * @author zhangbin by 31/08/2018 15:37
 * @since JDK 11
 */
public abstract class ThreadPoolWorker extends Thread {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    /**
     * 循环监听队列间隔时间:毫秒
     */
    private long spacingTime;

    /**
     * 该工作线程是否正在工作
     */
    private boolean isRunning = true;

    /**
     * 该工作线程是否可以执行新任务
     */
    private boolean isWaiting = true;

    /**
     * 空闲时间
     */
    long freeTime = 0;

    private final ThreadPoolService poolService;

    protected ThreadPoolWorker(ThreadPoolService poolService, long spacingTime) {
        this.poolService = poolService;
        this.spacingTime = spacingTime;
        this.start();
    }

    void stopWorker() {
        this.isRunning = false;
        stopCurrWorker();
    }

    /**
     * 循环执行任务 这也许是线程池的关键所在
     */
    public void run() {
        while (isRunning) {
            setWaiting(true);
            BaseThreadTask task = null;
            long start = System.currentTimeMillis();
            synchronized (poolService.taskQueue) {
                while (isRunning && poolService.taskQueue.isEmpty()) {
                    try {
                        poolService.taskQueue.wait(spacingTime);
                        freeTime += (System.currentTimeMillis() - start);
                    } catch (Exception ie) {
                        log.debug(ie.getMessage(), ie);
                    }
                }
                if (!poolService.taskQueue.isEmpty()) {
                    freeTime = 0;
                    task = poolService.taskQueue.remove(0);
                }
                poolService.taskQueue.notifyAll();
            }
            if (task != null) {
                log.debug("thread pool [" + poolService.poolName + "] thread " + getIndex() + " begin excute task:" + task.getTaskName());
                setWaiting(false);
                try {
                    task.setThreadindex(getIndex());
                    if (task.isNeedExecuteImmediate()) {
                        new Thread(task).start();
                    } else {
                        processTask(task);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                log.debug("thread pool [" + poolService.poolName + "] thread " + getIndex() + " excute task is finished:" + task.getTaskName());
                setWaiting(true);
            }
        }
    }

    int getIndex() {
        return poolService.workers.indexOf(this);
    }

    boolean isWaiting() {
        return isWaiting;
    }

    private void setWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    protected abstract void stopCurrWorker();

    protected abstract void processTask(BaseThreadTask task);

}
