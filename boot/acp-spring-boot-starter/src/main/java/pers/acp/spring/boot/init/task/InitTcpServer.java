package pers.acp.spring.boot.init.task;

import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.boot.conf.SocketListenerConfiguration;
import pers.acp.spring.boot.conf.TcpServerConfiguration;
import pers.acp.spring.boot.daemon.DaemonServiceManager;
import pers.acp.spring.boot.init.BaseInitTask;
import pers.acp.spring.boot.socket.tcp.TcpServer;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;

import java.util.List;

@Component
public final class InitTcpServer extends BaseInitTask {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final TcpServerConfiguration tcpServerConfiguration;

    private final List<ISocketServerHandle> socketServerHandleList;

    private final List<ByteToMessageDecoder> byteToMessageDecoderList;

    @Autowired(required = false)
    public InitTcpServer(TcpServerConfiguration tcpServerConfiguration, List<ISocketServerHandle> socketServerHandleList, List<ByteToMessageDecoder> byteToMessageDecoderList) {
        this.tcpServerConfiguration = tcpServerConfiguration;
        this.socketServerHandleList = socketServerHandleList;
        this.byteToMessageDecoderList = byteToMessageDecoderList;
    }

    public void startTcpServer() {
        log.info("start tcp listen service ...");
        if (socketServerHandleList != null && socketServerHandleList.size() > 0) {
            socketServerHandleList.forEach(BaseInitTask::addServerHandle);
        }
        if (byteToMessageDecoderList != null && byteToMessageDecoderList.size() > 0) {
            byteToMessageDecoderList.forEach(BaseInitTask::addMessageDecoder);
        }
        try {
            List<SocketListenerConfiguration> listens = tcpServerConfiguration.getListeners();
            if (listens != null && !listens.isEmpty()) {
                for (SocketListenerConfiguration listen : listens) {
                    if (listen.isEnabled()) {
                        String beanName = listen.getHandleBean();
                        if (!CommonTools.isNullStr(beanName)) {
                            ISocketServerHandle handle = getSocketServerHandle(beanName);
                            if (handle != null) {
                                int port = listen.getPort();
                                TcpServer tcpServer = new TcpServer(port, listen, handle, getMessageDecoder(listen.getMessageDecoder()));
                                Thread thread = new Thread(tcpServer);
                                thread.setDaemon(true);
                                thread.start();
                                DaemonServiceManager.addService(tcpServer);
                                log.info("start tcp listen service success [" + listen.getName() + "] , port:" + listen.getPort());
                            } else {
                                log.error("tcp handle bean [" + beanName + "] is invalid!");
                            }
                        }
                    } else {
                        log.info("tcp listen service is disabled [" + listen.getName() + "]");
                    }
                }
            } else {
                log.info("No tcp listen service was found");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("start tcp listen service finished!");
        }
    }

}
