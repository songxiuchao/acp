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
     * @param minThreadNumber 最小线程数
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(int minThreadNumber, int maxThreadNumber, int querySize) {
        return getInstance(60000, minThreadNumber, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param maxFreeTime     线程最大空闲时间，单位毫秒
     * @param minThreadNumber 最小线程数
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(long maxFreeTime, int minThreadNumber, int maxThreadNumber, int querySize) {
        return getInstance(null, maxFreeTime, minThreadNumber, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param minThreadNumber 最小线程数
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, int minThreadNumber, int maxThreadNumber, int querySize) {
        return getInstance(poolName, 60000, minThreadNumber, maxThreadNumber, querySize);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param maxFreeTime     线程最大空闲时间
     * @param minThreadNumber 最小线程数
     * @param maxThreadNumber 最大线程数
     * @param querySize       队列容量
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, long maxFreeTime, int minThreadNumber, int maxThreadNumber, int querySize) {
        ThreadPoolService instance;
        if (CommonUtils.isNullStr(poolName)) {
            poolName = "defaultThreadPool";
        }
        synchronized (ThreadPoolService.class) {
            if (!threadPoolInstanceMap.containsKey(poolName)) {
                instance = new ThreadPoolService(poolName, maxFreeTime, minThreadNumber, maxThreadNumber, querySize);
                log.debug("init ThreadPool [" + poolName + "] success, minThread:" + minThreadNumber + " maxThread:" + maxThreadNumber);
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
     * @param minThreadNumber 最小线程数
     * @param maxThreadNumber 最大线程数
     *                        Integer.MAX_VALUE--无界线程数，直接提交
     * @param querySize       队列容量；
     *                        Integer.MAX_VALUE--无界队列，LinkedBlockingQueue
     *                        else---------------有界队列，ArrayBlockingQueue
     */
    private ThreadPoolService(String poolName, long maxFreeTime, int minThreadNumber, int maxThreadNumber, int querySize) {
        this.poolName = poolName;
        if (maxThreadNumber == Integer.MAX_VALUE) {
            /*
             * 直接提交队列: SynchronousQueue
             * 1、要求无界 maximumPoolSizes 以避免拒绝新提交的任务。其中每个插入操作必须等待另一个线程的对应移除操作，也就是说A任务进入队列，B任务必须等A任务被移除之后才能进入队列，否则执行异常策略
             * 2、运行线程数 <  corePoolSize 时直接创建线程运行
             * 3、运行线程数 >= corePoolSize 任务放入队列
             * 4、队列中的任务处理逻辑：
             *    运行线程数 < maximumPoolSizes 时创建新线程运行待加入队列的任务
             *    运行线程数 = maximumPoolSizes 时执行异常策略
             * 5、当一个线程空闲时间超过 maxFreeTime 且运行线程数 > corePoolSize 时，销毁空闲线程，直至运行线程数 = corePoolSize
             */
            executor = new ThreadPoolExecutor(0, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        } else if (querySize == Integer.MAX_VALUE) {
            /*
             * 无界队列: LinkedBlockingQueue
             * 1、将导致在所有核心线程都在忙时新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize。(因此，maximumPoolSize 的值也就没意义了。)
             * 2、运行线程数 <  corePoolSize 时直接创建线程运行
             * 3、运行线程数 >= corePoolSize 任务放入队列，
             * 4、队列中的任务处理逻辑：
             *    在队列中一直排队等待，直至有空闲线程继续处理或内存溢出
             */
            executor = new ThreadPoolExecutor(maxThreadNumber, maxThreadNumber, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        } else {
            /*
             * 有界队列: ArrayBlockingQueue
             * 1、正常线程池，需适当调整 corePoolSize、maximumPoolSizes、querySize 的参数
             * 2、运行线程数 <  corePoolSize 时直接创建线程运行
             * 3、运行线程数 >= corePoolSize 任务放入队列
             * 4、队列中的任务处理逻辑：
             *    运行线程数 < maximumPoolSizes 且队列未满时（新任务加入队列前），新任务加入队列中一直排队等待
             *    运行线程数 < maximumPoolSizes 且队列已满时（新任务加入队列前），创建新线程运行待加入队列的任务
             *    运行线程数 = maximumPoolSizes 且队列未满时（新任务加入队列前），新任务加入队列中一直排队等待
             *    运行线程数 = maximumPoolSizes 且队列已满时（新任务加入队列前），执行异常策略
             * 5、当一个线程空闲时间超过 maxFreeTime 且运行线程数 > corePoolSize 时，销毁空闲线程，直至运行线程数 = corePoolSize
             */
            executor = new ThreadPoolExecutor(minThreadNumber, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(querySize));
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

    public static synchronized void stopAll() {
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
    public static synchronized void destroyAll() {
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
