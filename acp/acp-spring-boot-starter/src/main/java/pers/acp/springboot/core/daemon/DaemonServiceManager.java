package pers.acp.springboot.core.daemon;

import pers.acp.springboot.core.interfaces.IDaemonService;
import pers.acp.core.DBConTools;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.ThreadPoolService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by zhangbin on 2016/12/21.
 * 后台守护服务控制类
 */
public class DaemonServiceManager implements ServletContextListener {

    private static LogFactory log = LogFactory.getInstance(DaemonServiceManager.class);

    private static final ConcurrentLinkedDeque<IDaemonService> serverDeque = new ConcurrentLinkedDeque<>();

    /**
     * 添加后台守护服务
     *
     * @param daemonService 后台守护服务
     */
    public static void addService(IDaemonService daemonService) {
        synchronized (serverDeque) {
            if (!serverDeque.contains(daemonService)) {
                serverDeque.push(daemonService);
                log.info("add daemon service [" + daemonService.getServiceName() + "]");
            }
            serverDeque.notifyAll();
        }
    }

    /**
     * 停止后台守护服务
     */
    public static void stopAllService() {
        ThreadPoolService.destroyAll();
        synchronized (serverDeque) {
            while (!serverDeque.isEmpty()) {
                IDaemonService daemonService = serverDeque.pop();
                daemonService.stopService();
                log.info("destroy daemon service [" + daemonService.getServiceName() + "]");
            }
        }
        DBConTools.destroyAllConnections();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 服务器启动时执行初始化
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        stopAllService();
    }

}
