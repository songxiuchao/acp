package pers.acp.springboot.init.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.conf.SocketListenerConfiguration;
import pers.acp.springboot.conf.TcpServerConfiguration;
import pers.acp.springboot.daemon.DaemonServiceManager;
import pers.acp.springboot.socket.base.ISocketServerHandle;
import pers.acp.springboot.socket.tcp.TcpServer;
import pers.acp.springboot.tools.SpringBeanFactory;

import java.util.List;

@Component
public final class InitTcpServer {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final TcpServerConfiguration tcpServerConfiguration;

    @Autowired
    public InitTcpServer(TcpServerConfiguration tcpServerConfiguration) {
        this.tcpServerConfiguration = tcpServerConfiguration;
    }

    public void startTcpServer() {
        log.info("start tcp listen service ...");
        try {
            List<SocketListenerConfiguration> listens = tcpServerConfiguration.getListeners();
            if (!listens.isEmpty()) {
                for (SocketListenerConfiguration listen : listens) {
                    if (listen.isEnabled()) {
                        String beanName = listen.getHandleBean();
                        if (!CommonTools.isNullStr(beanName)) {
                            Object handleBean = SpringBeanFactory.getBean(beanName);
                            if (handleBean instanceof ISocketServerHandle) {
                                ISocketServerHandle handle = (ISocketServerHandle) handleBean;
                                int port = listen.getPort();
                                TcpServer tcpServer = new TcpServer(port, listen, handle);
                                Thread thread = new Thread(tcpServer);
                                thread.setDaemon(true);
                                thread.start();
                                DaemonServiceManager.addService(tcpServer);
                                log.info("start tcp listen service success [" + listen.getName() + "] , port:" + listen.getPort());
                            } else {
                                log.error("tcp response bean [" + beanName + "] is invalid!");
                            }
                        }
                    } else {
                        log.info("tcp listen service is disabled [" + listen.getName() + "]");
                    }
                }
            } else {
                log.info("No listen service was found");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start tcp listen service finished!");
        }
    }

}
