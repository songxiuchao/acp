package pers.acp.springboot.common.init;

import pers.acp.springboot.core.socket.base.BaseSocketHandle;
import pers.acp.springboot.core.socket.config.ListenConfig;
import pers.acp.springboot.core.socket.config.UdpConfig;
import pers.acp.springboot.core.socket.udp.UdpServer;
import pers.acp.springboot.core.tools.SpringBeanFactory;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.util.ArrayList;

final class InitUdpServer {

    private static final LogFactory log = LogFactory.getInstance(InitUdpServer.class);// 日志对象

    static void startUdpServer() {
        log.info("start udp listen service...");
        try {
            UdpConfig udpConfig = UdpConfig.getInstance();
            if (udpConfig != null) {
                ArrayList<ListenConfig> listens = (ArrayList<ListenConfig>) udpConfig.getListen();
                if (listens != null) {
                    for (ListenConfig listen : listens) {
                        if (listen.isEnabled()) {
                            String beanName = listen.getResponseBean();
                            if (!CommonTools.isNullStr(beanName)) {
                                Object responseBean = SpringBeanFactory.getBean(listen.getResponseBean());
                                if (responseBean instanceof BaseSocketHandle) {
                                    BaseSocketHandle udpResponse = (BaseSocketHandle) responseBean;
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
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
