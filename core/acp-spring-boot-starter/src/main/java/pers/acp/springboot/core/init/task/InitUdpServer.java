package pers.acp.springboot.core.init.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;
import pers.acp.springboot.core.conf.UdpServerConfiguration;
import pers.acp.springboot.core.socket.udp.UdpServer;
import pers.acp.springboot.core.tools.SpringBeanFactory;

import java.util.ArrayList;
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
                        String beanName = listen.getResponseBean();
                        if (!CommonTools.isNullStr(beanName)) {
                            Object responseBean = SpringBeanFactory.getBean(listen.getResponseBean());
                            if (responseBean instanceof ISocketServerHandle) {
                                ISocketServerHandle udpResponse = (ISocketServerHandle) responseBean;
                                int port = listen.getPort();
                                UdpServer server = new UdpServer(port, listen, udpResponse);
                                Thread sub = new Thread(server);
                                sub.setDaemon(true);
                                sub.start();
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
