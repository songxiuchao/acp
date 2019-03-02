package pers.acp.core.task.threadpool;

import pers.acp.core.log.LogFactory;

import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * @author zhangbin by 31/08/2018 15:53
 * @since JDK 11
 */
public class PoolWorkerDispatch implements Runnable {

    private static final LogFactory log = LogFactory.getInstance(ThreadPoolService.class);// 日志对象

    private final ThreadPoolService poolService;

    private final int minThreadNumber;

    private final int maxThreadNumber;

    private final long maxFreeTime;

    private final long spacingTime;

    private volatile boolean isRunning = false;

    private Class<? extends ThreadPoolWorker> poolWorkerClass;

    PoolWorkerDispatch(ThreadPoolService poolService, Class<? extends ThreadPoolWorker> poolWorkerClass, int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        this.poolService = poolService;
        this.poolWorkerClass = poolWorkerClass;
        this.minThreadNumber = minThreadNumber;
        this.maxFreeTime = maxFreeTime;
        this.maxThreadNumber = maxThreadNumber;
        this.spacingTime = spacingTime;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                synchronized (poolService.workers) {
                    if (poolService.workers.size() < minThreadNumber) {
                        for (int i = 0; i < (minThreadNumber - poolService.workers.size()); i++) {
                            Constructor constructor = poolWorkerClass.getConstructor(ThreadPoolService.class, long.class);
                            poolService.workers.add((ThreadPoolWorker) constructor.newInstance(poolService, spacingTime));
                        }
                        continue;
                    }
                    int queueSize = poolService.taskQueue.size();
                    if (queueSize > 0 && poolService.workers.size() < maxThreadNumber) {
                        int workerCount = queueSize;
                        if (workerCount > (maxThreadNumber - poolService.workers.size())) {
                            workerCount = maxThreadNumber - poolService.workers.size();
                        }
                        for (int i = 0; i < workerCount; i++) {
                            Constructor constructor = poolWorkerClass.getConstructor(ThreadPoolService.class, long.class);
                            poolService.workers.add((ThreadPoolWorker) constructor.newInstance(poolService, spacingTime));
                        }
                        continue;
                    }
                    int destroyCount = poolService.workers.size() - minThreadNumber;
                    Iterator<ThreadPoolWorker> it = poolService.workers.iterator();
                    while (it.hasNext() && destroyCount > 0) {
                        ThreadPoolWorker worker = it.next();
                        if (worker.freeTime > maxFreeTime) {
                            worker.stopWorker();
                            it.remove();
                            destroyCount--;
                        }
                    }
                }
                Thread.sleep(spacingTime);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                stopWork();
                break;
            }
        }
    }

    void stopWork() {
        isRunning = false;
        synchronized (poolService.workers) {
            for (ThreadPoolWorker worker : poolService.workers) {
                worker.stopWorker();
            }
        }
    }

}
