package pers.acp.springboot.core.socket.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;
import pers.acp.core.log.LogFactory;

import java.net.InetSocketAddress;

public final class TcpServer extends IoHandlerAdapter implements Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private int port;

    private SocketListenerConfiguration socketListenerConfiguration;

    private ISocketServerHandle socketServerHandle;

    /**
     * 构造函数
     *
     * @param port               端口
     * @param socketListenerConfiguration       监听服务配置
     * @param socketServerHandle 接收报文处理对象
     */
    public TcpServer(int port, SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle) {
        this.port = port;
        this.socketListenerConfiguration = socketListenerConfiguration;
        this.socketServerHandle = socketServerHandle;
    }

    @Override
    public void run() {
        if (this.socketServerHandle != null) {
            NioSocketAcceptor acceptor = new NioSocketAcceptor();
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
            log.error("tcp listen server is stop,case by:response object is null[BaseSocketHandle]");
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
        if (socketListenerConfiguration.isHex()) {
            recvStr = ByteUtils.toHexString(byten);
        } else {
            recvStr = new String(byten, socketListenerConfiguration.getCharset());
        }
        log.debug("tcp receive:" + recvStr);
        TcpServerHandle handle = new TcpServerHandle(session, socketListenerConfiguration, socketServerHandle, recvStr);
        Thread thread = new Thread(handle);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        log.debug("tcp server session closed");
        if (session != null) {
            session.closeNow();
        }
        socketServerHandle.sessionClosed(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        if (session != null) {
            session.closeNow();
        }
        socketServerHandle.exceptionCaught(session, cause);
    }

    @Override
    public void messageSent(IoSession session, Object obj) throws Exception {
        super.messageSent(session, obj);
        if (!socketListenerConfiguration.isKeepAlive()) {
            if (session != null) {
                session.closeNow();
            }
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        SocketSessionConfig config = (SocketSessionConfig) session.getConfig();
        config.setBothIdleTime((int) (socketListenerConfiguration.getIdletime() / 1000));
        config.setWriterIdleTime((int) (socketListenerConfiguration.getIdletime() / 1000));
        config.setReaderIdleTime((int) (socketListenerConfiguration.getIdletime() / 1000));
        config.setKeepAlive(socketListenerConfiguration.isKeepAlive());
        if (socketListenerConfiguration.isKeepAlive()) {
            config.setSoLinger(0);
        }
        socketServerHandle.sessionCreated(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception {
        super.sessionIdle(session, idlestatus);
        log.debug("tcp server session idle");
        socketServerHandle.sessionIdle(session, idlestatus);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        socketServerHandle.sessionOpened(session);
    }

}
