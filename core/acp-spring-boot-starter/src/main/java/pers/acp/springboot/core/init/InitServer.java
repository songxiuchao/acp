package pers.acp.springboot.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.DBConTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.init.task.InitTcpServer;
import pers.acp.springboot.core.init.task.InitUdpServer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InitServer {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final InitTcpServer initTcpServer;

    private final InitUdpServer initUdpServer;

    @Autowired
    public InitServer(InitTcpServer initTcpServer, InitUdpServer initUdpServer) {
        this.initTcpServer = initTcpServer;
        this.initUdpServer = initUdpServer;
    }

    /**
     * 主线程中进行系统初始化
     */
    void startNow() {
        try {
            InitSource.getInstance(initTcpServer, initUdpServer).run();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }

    /**
     * 新线程中进行系统初始化
     */
    void startDelay() {
        try {
            InitSource.getInstance(initTcpServer, initUdpServer).start();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }
}

class InitSource extends Thread {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private volatile static InitSource handle = null;

    private final InitTcpServer initTcpServer;

    private final InitUdpServer initUdpServer;

    private InitSource(InitTcpServer initTcpServer, InitUdpServer initUdpServer) {
        this.setDaemon(true);
        this.initTcpServer = initTcpServer;
        this.initUdpServer = initUdpServer;
    }

    protected static InitSource getInstance(InitTcpServer initTcpServer, InitUdpServer initUdpServer) {
        if (handle == null) {
            synchronized (InitSource.class) {
                if (handle == null) {
                    handle = new InitSource(initTcpServer, initUdpServer);
                }
            }
        }
        return handle;
    }

    public void run() {
        try {
            /* 初始化系统服务 */
            initTcpServer.startTcpServer();
            initUdpServer.startUdpServer();
            initTools();
        } catch (Exception e) {
            log.error("init service Exception:" + e.getMessage(), e);
        }
    }

    private void initTools() {
        log.info("tools init begin ...");
        CommonTools.initTools();
        DBConTools.initTools();
        log.info("tools init finished!");
    }

}