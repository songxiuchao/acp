package pers.acp.spring.boot.init.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.boot.conf.SocketListenerConfiguration;
import pers.acp.spring.boot.conf.UdpServerConfiguration;
import pers.acp.spring.boot.daemon.DaemonServiceManager;
import pers.acp.spring.boot.socket.udp.UdpServer;
import pers.acp.spring.boot.tools.SpringBeanFactory;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;

import java.util.List;

@Component
public final class InitUdpServer {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final UdpServerConfiguration udpServerConfiguration;

    @Autowired
    public InitUdpServer(UdpServerConfiguration udpServerConfiguration) {
        this.udpServerConfiguration = udpServerConfiguration;
    }

    public void startUdpServer() {
        log.info("start udp listen service ...");
        try {
            List<SocketListenerConfiguration> listens = udpServerConfiguration.getListeners();
            if (!listens.isEmpty()) {
                for (SocketListenerConfiguration listen : listens) {
                    if (listen.isEnabled()) {
                        String beanName = listen.getHandleBean();
                        if (!CommonTools.isNullStr(beanName)) {
                            Object responseBean = SpringBeanFactory.getBean(listen.getHandleBean());
                            if (responseBean instanceof ISocketServerHandle) {
                                ISocketServerHandle udpResponse = (ISocketServerHandle) responseBean;
                                int port = listen.getPort();
                                UdpServer server = new UdpServer(port, listen, udpResponse);
                                Thread sub = new Thread(server);
                                sub.setDaemon(true);
                                sub.start();
                                DaemonServiceManager.addService(server);
                                log.info("start udp listen service success [" + listen.getName() + "] , port:" + listen.getPort());
                            } else {
                                log.error("udp response bean [" + beanName + "] is invalid!");
                            }
                        }
                    } else {
                        log.info("udp listen service is disabled [" + listen.getName() + "]");
                    }
                }
            } else {
                log.info("No listen service was found");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start udp listen service finished!");
        }
    }

}
