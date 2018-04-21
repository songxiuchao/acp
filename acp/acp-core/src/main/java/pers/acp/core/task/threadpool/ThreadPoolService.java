package pers.acp.core.task.threadpool;

import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.core.tools.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程池调度
 *
 * @author zb
 */
public final class ThreadPoolService {

    private static final LogFactory log = LogFactory.getInstance(ThreadPoolService.class);// 日志对象

    private static final ConcurrentHashMap<String, ThreadPoolService> threadPoolInstanceMap = new ConcurrentHashMap<>();

    private String poolName;

    private final CopyOnWriteArrayList<BaseThreadTask> taskQueue = new CopyOnWriteArrayList<>();

    private final List<PoolWorker> workers = new ArrayList<>();

    private PoolWorkerDispatch workDispatch;

    /**
     * 获取线程池实例
     *
     * @param spacingTime     轮询队列的间隔时间
     * @param maxThreadNumber 最大线程数
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(long spacingTime, int maxThreadNumber) {
        return getInstance(null, spacingTime, maxThreadNumber);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param spacingTime     轮询队列的间隔时间
     * @param maxThreadNumber 最大线程数
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, long spacingTime, int maxThreadNumber) {
        ThreadPoolService instance;
        if (CommonUtils.isNullStr(poolName)) {
            poolName = "defaultThreadPool";
        }
        synchronized (ThreadPoolService.class) {
            if (!threadPoolInstanceMap.containsKey(poolName)) {
                instance = new ThreadPoolService(poolName, spacingTime, maxThreadNumber);
                log.debug("init ThreadPool [" + poolName + "] success, max thread:" + maxThreadNumber);
                threadPoolInstanceMap.put(poolName, instance);
            } else {
                instance = threadPoolInstanceMap.get(poolName);
            }
        }
        return instance;
    }

    /**
     * 销毁所有线程池
     */
    public static void destroyAll() {
        synchronized (ThreadPoolService.class) {
            threadPoolInstanceMap.forEach((key, poolService) -> poolService.destroy());
        }
    }

    /**
     * 线程池实例构造函数
     *
     * @param poolName        线程池实例名
     * @param spacingTime     轮询队列的间隔时间
     * @param maxThreadNumber 最大线程数
     */
    private ThreadPoolService(String poolName, long spacingTime, int maxThreadNumber) {
        this.poolName = poolName;
        workDispatch = new PoolWorkerDispatch(1, 60000, maxThreadNumber, spacingTime);
        workDispatch.start();
    }

    /**
     * 获取线程池信息
     *
     * @return 线程池信息
     */
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Thread Pool instance : ").append(poolName).append("\n");
        sb.append("Task Queue Size : ").append(taskQueue.size()).append("\n");
        for (BaseThreadTask aTaskQueue : taskQueue) {
            sb.append("Task ").append(aTaskQueue.getTaskId()).append(" is ").append((aTaskQueue.isRunning()) ? "Running.\n" : "Waiting.\n");
        }
        sb.append("workerThread Number:").append(workers.size()).append("\n");
        for (PoolWorker worker : workers) {
            sb.append("WorkerThread ").append(worker.getIndex()).append(" is ").append(worker.isWaiting() ? "Waiting.\n" : "Running.\n");
        }
        return sb.toString();
    }

    /**
     * 销毁线程池
     */
    public synchronized void destroy() {
        taskQueue.clear();
        workDispatch.stopWork();
        threadPoolInstanceMap.remove(poolName);
        log.debug("thread pool [" + poolName + "] is destroyed");
    }

    /**
     * 增加新的任务 每增加一个新任务,都要唤醒任务队列
     *
     * @param newTask 任务
     */
    public void addTask(BaseThreadTask newTask) {
        synchronized (taskQueue) {
            newTask.setSubmitTime(new Date());
            taskQueue.add(newTask);
            taskQueue.notifyAll();
        }
        log.debug("thread pool [" + poolName + "] submit task[" + newTask.getTaskId() + "]: " + newTask.getTaskName());
    }

    /**
     * 批量增加新任务
     *
     * @param taskes 任务
     */
    public void batchAddTask(BaseThreadTask[] taskes) {
        if (taskes == null || taskes.length == 0) {
            return;
        }
        synchronized (taskQueue) {
            for (BaseThreadTask taske : taskes) {
                if (taske == null) {
                    continue;
                }
                taske.setSubmitTime(new Date());
                taskQueue.add(taske);
                log.debug("thread pool [" + poolName + "] submit task[" + taske.getTaskId() + "]: " + taske.getTaskName());
            }
            taskQueue.notifyAll();
        }
    }

    /**
     * 获取线程池名称
     *
     * @return 线程池名称
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * 线程池任务队列是否为空
     *
     * @return 任务队列是否为空
     */
    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    /**
     * 所有线程是否处于等待状态
     *
     * @return 是否处于等待状态
     */
    public boolean isWaitingAll() {
        for (PoolWorker worker : workers) {
            if (!worker.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指定线程是否处于等待状态
     *
     * @param threadindex 线程编号
     * @return 是否处于等待状态
     */
    public boolean isWaitingCurr(int threadindex) {
        return workers.get(threadindex).isWaiting();
    }

    /**
     * 指定线程以外的其他线程是否处于等待状态
     *
     * @param threadindex 线程编号
     * @return 是否处于等待状态
     */
    public boolean isWaitingOther(int threadindex) {
        for (PoolWorker worker : workers) {
            if (worker.getIndex() != threadindex && !worker.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 池中工作线程
     *
     * @author zb
     */
    private class PoolWorker extends Thread {

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
        private long freeTime = 0;

        private PoolWorker(long spacingTime) {
            this.spacingTime = spacingTime;
            this.start();
        }

        private void stopWorker() {
            this.isRunning = false;
        }

        /**
         * 循环执行任务 这也许是线程池的关键所在
         */
        public void run() {
            while (isRunning) {
                setWaiting(true);
                BaseThreadTask task = null;
                long start = System.currentTimeMillis();
                synchronized (taskQueue) {
                    while (isRunning && taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait(spacingTime);
                            freeTime += (System.currentTimeMillis() - start);
                        } catch (Exception ie) {
                            log.debug(ie.getMessage(), ie);
                        }
                    }
                    if (!taskQueue.isEmpty()) {
                        freeTime = 0;
                        task = taskQueue.remove(0);
                    }
                    taskQueue.notifyAll();
                }
                if (task != null) {
                    log.debug("thread pool [" + poolName + "] thread " + getIndex() + " begin excute task:" + task.getTaskName());
                    setWaiting(false);
                    try {
                        task.setThreadindex(getIndex());
                        if (task.isNeedExecuteImmediate()) {
                            new Thread(task).start();
                        } else {
                            task.doExcute();
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    log.debug("thread pool [" + poolName + "] thread " + getIndex() + " excute task is finished:" + task.getTaskName());
                    setWaiting(true);
                }
            }
        }

        private int getIndex() {
            return workers.indexOf(this);
        }

        private boolean isWaiting() {
            return isWaiting;
        }

        private void setWaiting(boolean isWaiting) {
            this.isWaiting = isWaiting;
        }

    }

    /**
     * 工作线程调度线程
     */
    private class PoolWorkerDispatch extends Thread {

        private final int minThreadNumber;

        private final int maxThreadNumber;

        private final long maxFreeTime;

        private final long spacingTime;

        private boolean isRunning = false;

        private PoolWorkerDispatch(int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
            this.minThreadNumber = minThreadNumber;
            this.maxFreeTime = maxFreeTime;
            this.maxThreadNumber = maxThreadNumber;
            this.spacingTime = spacingTime;
        }

        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                synchronized (workers) {
                    if (workers.size() < minThreadNumber) {
                        for (int i = 0; i < (minThreadNumber - workers.size()); i++) {
                            workers.add(new PoolWorker(spacingTime));
                        }
                        continue;
                    }
                    int queueSize = taskQueue.size();
                    if (queueSize > 0 && workers.size() < maxThreadNumber) {
                        int workerCount = queueSize;
                        if (workerCount > (maxThreadNumber - workers.size())) {
                            workerCount = maxThreadNumber - workers.size();
                        }
                        for (int i = 0; i < workerCount; i++) {
                            workers.add(new PoolWorker(spacingTime));
                        }
                        continue;
                    }
                    int destroyCount = workers.size() - minThreadNumber;
                    Iterator<PoolWorker> it = workers.iterator();
                    while (it.hasNext() && destroyCount > 0) {
                        PoolWorker worker = it.next();
                        if (worker.freeTime > maxFreeTime) {
                            worker.stopWorker();
                            it.remove();
                            destroyCount--;
                        }
                    }
                }
                try {
                    sleep(spacingTime);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        private void stopWork() {
            isRunning = false;
            synchronized (workers) {
                for (PoolWorker worker : workers) {
                    worker.stopWorker();
                }
            }
        }

    }

}
