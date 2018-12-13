package pers.acp.springboot.core.init.task;

import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.socket.config.ListenConfig;
import pers.acp.springboot.core.socket.config.TcpConfig;
import pers.acp.springboot.core.socket.tcp.TcpServer;
import pers.acp.springboot.core.tools.SpringBeanFactory;

import java.util.List;

public final class InitTcpServer {

    private static final LogFactory log = LogFactory.getInstance(InitTcpServer.class);// 日志对象

    public static void startTcpServer() {
        log.info("start tcp listen service...");
        try {
            TcpConfig tcpConfig = TcpConfig.getInstance();
            if (tcpConfig != null) {
                List<ListenConfig> listens = tcpConfig.getListen();
                if (listens != null) {
                    for (ListenConfig listen : listens) {
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
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
