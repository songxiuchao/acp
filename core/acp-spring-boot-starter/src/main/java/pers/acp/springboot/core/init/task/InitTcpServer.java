package pers.acp.springboot.core.init.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;
import pers.acp.springboot.core.conf.TcpServerConfiguration;
import pers.acp.springboot.core.socket.tcp.TcpServer;
import pers.acp.springboot.core.tools.SpringBeanFactory;

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
                        String beanName = listen.getResponseBean();
                        if (!CommonTools.isNullStr(beanName)) {
                            Object responseBean = SpringBeanFactory.getBean(listen.getResponseBean());
                            if (responseBean instanceof ISocketServerHandle) {
                                ISocketServerHandle tcpResponse = (ISocketServerHandle) responseBean;
                                int port = listen.getPort();
                                TcpServer server = new TcpServer(port, listen, tcpResponse);
                                Thread sub = new Thread(server);
                                sub.setDaemon(true);
                                sub.start();
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
