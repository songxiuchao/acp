package pers.acp.springboot.core.socket.udp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.springboot.core.socket.base.BaseSocketHandle;
import pers.acp.springboot.core.socket.config.ListenConfig;
import pers.acp.core.log.LogFactory;

import java.net.InetSocketAddress;

public final class UdpServer extends IoHandlerAdapter implements Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private int port;

    private ListenConfig listenConfig;

    private BaseSocketHandle socketResponse;

    /**
     * 构造函数
     *
     * @param port           端口
     * @param listenConfig   监听服务配置
     * @param socketResponse 接收报文处理对象
     */
    public UdpServer(int port, ListenConfig listenConfig, BaseSocketHandle socketResponse) {
        this.port = port;
        this.listenConfig = listenConfig;
        this.socketResponse = socketResponse;
    }

    @Override
    public void run() {
        if (this.socketResponse != null) {
            NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
            LoggingFilter loggingFilter = new LoggingFilter();
            loggingFilter.setSessionClosedLogLevel(LogLevel.DEBUG);
            loggingFilter.setSessionCreatedLogLevel(LogLevel.DEBUG);
            loggingFilter.setSessionOpenedLogLevel(LogLevel.DEBUG);
            loggingFilter.setSessionIdleLogLevel(LogLevel.DEBUG);
            loggingFilter.setMessageSentLogLevel(LogLevel.DEBUG);
            loggingFilter.setMessageReceivedLogLevel(LogLevel.DEBUG);
            acceptor.getFilterChain().addLast("logger", loggingFilter);
            acceptor.setHandler(this);
            try {
                acceptor.bind(new InetSocketAddress(port));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.error("udp listen server is stop,case by:response object is null[BaseSocketHandle]");
        }
    }

    public int getPort() {
        return port;
    }


    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String recvStr;
        if (listenConfig.isHex()) {
            recvStr = ByteUtils.toHexString(byten);
        } else {
            recvStr = new String(byten, listenConfig.getCharset());
        }
        log.debug("udp receive:" + recvStr);
        UdpServerHandle handle = new UdpServerHandle(session, listenConfig, socketResponse, recvStr);
        Thread thread = new Thread(handle);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        super.exceptionCaught(session, cause);
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void messageSent(IoSession session, Object obj) throws Exception {
        super.messageSent(session, obj);
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        SocketSessionConfig config = (SocketSessionConfig) session.getConfig();
        config.setIdleTime(IdleStatus.BOTH_IDLE, (int) (listenConfig.getIdletime() / 1000));
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception {
        super.sessionIdle(session, idlestatus);
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

}
