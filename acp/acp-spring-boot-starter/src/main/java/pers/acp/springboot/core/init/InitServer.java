package pers.acp.springboot.core.init;

import pers.acp.core.CommonTools;
import pers.acp.core.DBConTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.daemon.DaemonServiceManager;
import pers.acp.springboot.core.init.task.InitTcpServer;
import pers.acp.springboot.core.init.task.InitUdpServer;

class InitServer {

    private static final LogFactory log = LogFactory.getInstance(InitServer.class);// 日志对象

    /**
     * 主线程中进行系统初始化
     */
    static void startNow() {
        try {
            InitSource.getInstance().run();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }

    /**
     * 新线程中进行系统初始化
     */
    static void startDelay() {
        try {
            InitSource.getInstance().start();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }
}

class InitSource extends Thread {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private static InitSource handle = null;

    private InitSource() {
        this.setDaemon(true);
    }

    protected static InitSource getInstance() {
        if (handle == null) {
            synchronized (InitSource.class) {
                if (handle == null) {
                    handle = new InitSource();
                }
            }
        }
        return handle;
    }

    public void run() {
        log.info("begin start-up...");
        try {
            /* 初始化系统 start *******/
            /* 初始化系统服务 */
            InitTcpServer.startTcpServer();
            InitUdpServer.startUdpServer();
            initTools();
            log.info("finish start-up");
            log.info("****************** system is started ******************");
            /* 初始化系统 end *******/
            Runtime.getRuntime().addShutdownHook(new Thread(DaemonServiceManager::stopAllService));
        } catch (Exception e) {
            log.error("init service Exception:" + e.getMessage(), e);
        }
    }

    private void initTools() {
        log.info("Tools init begin...");
        CommonTools.initTools();
        DBConTools.initTools();
        log.info("Tools init finished!");
    }

}