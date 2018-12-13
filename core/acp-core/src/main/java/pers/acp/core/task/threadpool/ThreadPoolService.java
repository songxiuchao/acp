package pers.acp.core.task.threadpool;

import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.core.task.threadpool.worker.DefaultPoolWorker;
import pers.acp.core.tools.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
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

    String poolName;

    final CopyOnWriteArrayList<BaseThreadTask> taskQueue = new CopyOnWriteArrayList<>();

    final List<ThreadPoolWorker> workers = new ArrayList<>();

    private PoolWorkerDispatch workDispatch;

    /**
     * 获取线程池实例
     *
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(int maxThreadNumber, long spacingTime) {
        return getInstance(1, 60000, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param minThreadNumber 最小线程数
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        return getInstance(null, DefaultPoolWorker.class, minThreadNumber, maxFreeTime, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, long spacingTime, int maxThreadNumber) {
        return getInstance(poolName, 1, 60000, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param minThreadNumber 最小线程数
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        return getInstance(poolName, DefaultPoolWorker.class, minThreadNumber, maxFreeTime, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param poolWorkerClass 工作线程类
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(Class<? extends ThreadPoolWorker> poolWorkerClass, long spacingTime, int maxThreadNumber) {
        return getInstance(null, poolWorkerClass, 1, 60000, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param poolWorkerClass 工作线程类
     * @param minThreadNumber 最小线程数
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(Class<? extends ThreadPoolWorker> poolWorkerClass, int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        return getInstance(null, poolWorkerClass, minThreadNumber, maxFreeTime, maxThreadNumber, spacingTime);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param poolWorkerClass 工作线程类
     * @param minThreadNumber 最小线程数
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, Class<? extends ThreadPoolWorker> poolWorkerClass, int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        ThreadPoolService instance;
        if (CommonUtils.isNullStr(poolName)) {
            poolName = "defaultThreadPool";
        }
        synchronized (ThreadPoolService.class) {
            if (!threadPoolInstanceMap.containsKey(poolName)) {
                instance = new ThreadPoolService(poolName, poolWorkerClass, minThreadNumber, maxFreeTime, maxThreadNumber, spacingTime);
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
     * @param poolWorkerClass 工作线程类
     * @param minThreadNumber 最小线程数
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param spacingTime     轮询队列的间隔时间
     */
    private ThreadPoolService(String poolName, Class<? extends ThreadPoolWorker> poolWorkerClass, int minThreadNumber, long maxFreeTime, int maxThreadNumber, long spacingTime) {
        this.poolName = poolName;
        workDispatch = new PoolWorkerDispatch(this, poolWorkerClass, minThreadNumber, maxFreeTime, maxThreadNumber, spacingTime);
        Thread thread = new Thread(workDispatch);
        thread.setDaemon(true);
        thread.start();
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
        for (ThreadPoolWorker worker : workers) {
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
        for (ThreadPoolWorker worker : workers) {
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
        for (ThreadPoolWorker worker : workers) {
            if (worker.getIndex() != threadindex && !worker.isWaiting()) {
                return false;
            }
        }
        return true;
    }

}
