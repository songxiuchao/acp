package pers.acp.core.task.threadpool;

import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.core.tools.CommonUtils;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 线程池调度
 *
 * @author zb
 */
public final class ThreadPoolService {

    private static final LogFactory log = LogFactory.getInstance(ThreadPoolService.class);// 日志对象

    private static final ConcurrentHashMap<String, ThreadPoolService> threadPoolInstanceMap = new ConcurrentHashMap<>();

    private String poolName;

    private ThreadPoolExecutor executor;

    /**
     * 获取线程池实例
     *
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(int maxThreadNumber, int querySize) {
        return getInstance(60000, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param maxFreeTime     线程最大空闲时间，单位毫秒
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(long maxFreeTime, int maxThreadNumber, int querySize) {
        return getInstance(null, maxFreeTime, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, int maxThreadNumber, int querySize) {
        return getInstance(poolName, 60000, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, long maxFreeTime, int maxThreadNumber, int querySize) {
        ThreadPoolService instance;
        if (CommonUtils.isNullStr(poolName)) {
            poolName = "defaultThreadPool";
        }
        synchronized (ThreadPoolService.class) {
            if (!threadPoolInstanceMap.containsKey(poolName)) {
                instance = new ThreadPoolService(poolName, maxFreeTime, maxThreadNumber, querySize);
                log.debug("init ThreadPool [" + poolName + "] success, maxThread:" + maxThreadNumber);
                threadPoolInstanceMap.put(poolName, instance);
            } else {
                instance = threadPoolInstanceMap.get(poolName);
            }
        }
        return instance;
    }

    /**
     * 线程池实例构造函数
     *
     * @param poolName        线程池实例名
     * @param maxFreeTime     线程最大空闲时间
     * @param maxThreadNumber 最大线程数
     *                        Integer.MAX_VALUE--无界线程数，直接提交
     * @param querySize       队列容量；
     *                        Integer.MAX_VALUE--无界队列，LinkedBlockingQueue
     *                        else---------------有界队列，ArrayBlockingQueue
     */
    private ThreadPoolService(String poolName, long maxFreeTime, int maxThreadNumber, int querySize) {
        this.poolName = poolName;
        if (maxThreadNumber == Integer.MAX_VALUE) {// 直接提交
            executor = new ThreadPoolExecutor(0, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        } else if (querySize == Integer.MAX_VALUE) {// 无界队列
            executor = new ThreadPoolExecutor(maxThreadNumber, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        } else {// 有界队列
            executor = new ThreadPoolExecutor(0, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(querySize));
        }
    }

    public synchronized void stop() {
        this.executor.shutdown();
        long start = System.currentTimeMillis();
        try {
            while (!this.executor.awaitTermination(1, TimeUnit.SECONDS)) {
                log.info("pool [" + poolName + "] there are still unfinished tasks...");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        threadPoolInstanceMap.remove(poolName);
        log.info("thread pool [" + poolName + "] is stoped. It took " + (end - start) + " millisecond");
    }

    public synchronized void stopAll() {
        synchronized (ThreadPoolService.class) {
            threadPoolInstanceMap.forEach((key, poolService) -> poolService.stop());
        }
    }

    /**
     * 销毁线程池
     */
    public synchronized void destroy() {
        this.executor.shutdownNow();
        threadPoolInstanceMap.remove(poolName);
        log.info("thread pool [" + poolName + "] is destroyed");
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
     * 增加新的任务 每增加一个新任务,都要唤醒任务队列
     *
     * @param newTask 任务
     */
    public void addTask(BaseThreadTask newTask) {
        newTask.setSubmitTime(new Date());
        this.executor.execute(newTask);
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
        for (BaseThreadTask taske : taskes) {
            if (taske == null) {
                continue;
            }
            taske.setSubmitTime(new Date());
            this.executor.execute(taske);
            log.debug("thread pool [" + poolName + "] submit task[" + taske.getTaskId() + "]: " + taske.getTaskName());
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
        return this.executor.getQueue().isEmpty();
    }

}
