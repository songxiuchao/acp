package pers.acp.spring.boot.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.boot.base.BaseInitialization;
import pers.acp.spring.boot.component.SystemControl;
import pers.acp.spring.boot.daemon.DaemonServiceManager;

/**
 * 系统初始化
 * Created by zhangbin on 2017-6-16.
 */
@Component
public class SystemInitialization extends BaseInitialization {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final SystemControl systemControl;

    private final InitServer initServer;

    @Autowired
    public SystemInitialization(SystemControl systemControl, InitServer initServer) {
        this.systemControl = systemControl;
        this.initServer = initServer;
    }

    @Override
    public String getName() {
        return "system initialization";
    }

    @Override
    public void start() {
        log.info(">>>>>>>>>>>>>>>>>>>> system is starting ...");
        /* 启动初始化服务 */
        initServer.startNow();
        /* 启动 listener 及定时任务 */
        systemControl.initialization();
        Runtime.getRuntime().addShutdownHook(new Thread(DaemonServiceManager::stopAllService));
        log.info(">>>>>>>>>>>>>>>>>>>> system start finished!");
    }

    @Override
    public void stop() {
        systemControl.stop();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
