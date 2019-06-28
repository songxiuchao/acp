package pers.acp.spring.boot.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.boot.conf.AcpCoreConfiguration;
import pers.acp.spring.boot.init.task.InitTcpServer;
import pers.acp.spring.boot.init.task.InitUdpServer;

/**
 * 初始化系统及TCP、UDP服务
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InitServer {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final InitTcpServer initTcpServer;

    private final InitUdpServer initUdpServer;

    private final AcpCoreConfiguration acpCoreConfiguration;

    @Autowired
    public InitServer(InitTcpServer initTcpServer, InitUdpServer initUdpServer, AcpCoreConfiguration acpCoreConfiguration) {
        this.initTcpServer = initTcpServer;
        this.initUdpServer = initUdpServer;
        this.acpCoreConfiguration = acpCoreConfiguration;
    }

    /**
     * 主线程中进行系统初始化
     */
    void startNow() {
        try {
            InitSource.getInstance(initTcpServer, initUdpServer, acpCoreConfiguration).run();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }

    /**
     * 新线程中进行系统初始化
     */
    void startDelay() {
        try {
            InitSource.getInstance(initTcpServer, initUdpServer, acpCoreConfiguration).start();
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

    private final AcpCoreConfiguration acpCoreConfiguration;

    private InitSource(InitTcpServer initTcpServer, InitUdpServer initUdpServer, AcpCoreConfiguration acpCoreConfiguration) {
        this.acpCoreConfiguration = acpCoreConfiguration;
        this.setDaemon(true);
        this.initTcpServer = initTcpServer;
        this.initUdpServer = initUdpServer;
    }

    protected static InitSource getInstance(InitTcpServer initTcpServer, InitUdpServer initUdpServer, AcpCoreConfiguration acpCoreConfiguration) {
        if (handle == null) {
            synchronized (InitSource.class) {
                if (handle == null) {
                    handle = new InitSource(initTcpServer, initUdpServer, acpCoreConfiguration);
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
        CommonTools.initTools(acpCoreConfiguration.getDeleteFileWaitTime(),
                acpCoreConfiguration.getAbsPathPrefix(),
                acpCoreConfiguration.getUserPathPrefix(),
                acpCoreConfiguration.getFontPath());
        log.info("tools init finished!");
    }

}