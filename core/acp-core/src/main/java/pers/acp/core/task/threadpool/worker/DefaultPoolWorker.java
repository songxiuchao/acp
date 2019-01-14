package pers.acp.core.task.threadpool.worker;

import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.ThreadPoolWorker;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

/**
 * @author zhangbin by 31/08/2018 15:43
 * @since JDK 11
 */
public class DefaultPoolWorker extends ThreadPoolWorker {

    public DefaultPoolWorker(ThreadPoolService poolService, long spacingTime) {
        super(poolService, spacingTime);
    }

    @Override
    protected void stopCurrWorker() {

    }

    @Override
    protected void processTask(BaseThreadTask task) {
        task.doExcute();
    }

}
