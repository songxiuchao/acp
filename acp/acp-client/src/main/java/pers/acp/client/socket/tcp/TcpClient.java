package pers.acp.client.socket.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.client.socket.ISocketHandle;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

public final class TcpClient extends IoHandlerAdapter {

    private LogFactory log = LogFactory.getInstance(this.getClass());

    private String serverIp;

    private int port;

    private int timeOut;

    private String serverCharset = CommonTools.getDefaultCharset();

    private boolean isHex = false;

    private ISocketHandle socketHandle = null;

    private boolean keepAlive = false;

    private IoConnector connector = null;

    private IoSession session = null;

    /**
     * 创建socket发送客户端
     *
     * @param serverIp 发送IP
     * @param port     发送端口
     * @param timeOut  接收超时时间
     */
    public TcpClient(String serverIp, int port, int timeOut) {
        this.serverIp = serverIp;
        this.port = port;
        int MAX_TIMEOUT = 3600000;
        if (timeOut < MAX_TIMEOUT) {
            this.timeOut = timeOut;
        } else {
            this.timeOut = MAX_TIMEOUT;
        }
    }

    /**
     * 手动关闭连接
     */
    public void doClose() {
        if (session != null) {
            session.closeNow();
            session = null;
        }
        if (connector != null) {
            connector.dispose();
            connector = null;
        }
    }

    /**
     * 手动关闭连接
     */
    public void doCloseOnFlush() {
        if (session != null) {
            session.closeOnFlush();
            session = null;
        }
        if (connector != null) {
            connector.dispose(true);
            connector = null;
        }
    }

    /**
     * 配置信息
     */
    private void setUpConfig() {
        connector.setConnectTimeoutMillis(timeOut);
        SocketSessionConfig config = (SocketSessionConfig) connector.getSessionConfig();
        config.setWriteTimeout(timeOut / 1000);
        config.setKeepAlive(keepAlive);
        if (keepAlive) {
            config.setSoLinger(0);
        } else {
            config.setBothIdleTime(timeOut / 1000);
        }
    }

    /**
     * 同步发送报文
     *
     * @param mess     报文字符串
     * @param needRead 是否需要接收返回信息
     * @return 响应报文
     */
    public String doSend(final String mess, boolean needRead) {
        try {
            if (connector == null || session == null || connector.isDisposed() || !session.isConnected() || connector.isDisposing() || session.isClosing()) {
                connector = new NioSocketConnector();
                connector.getSessionConfig().setUseReadOperation(true);
                setUpConfig();
                session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
                log.debug("connect tcp server[" + serverIp + ":port] timeOut:" + timeOut);
            }
            byte[] bts;
            if (isHex) {
                bts = ByteUtils.fromHexString(mess);
            } else {
                bts = mess.getBytes(serverCharset);
            }
            IoBuffer buffer = IoBuffer.allocate(bts.length);
            buffer.setAutoExpand(true);
            buffer.setAutoShrink(true);
            buffer.put(bts);
            buffer.flip();
            session.write(buffer).awaitUninterruptibly();
            log.debug("tcp send:" + mess);
            String recvStr = "";
            if (needRead) {
                ReadFuture readFuture = session.read();
                if (readFuture.awaitUninterruptibly(timeOut, TimeUnit.MILLISECONDS)) {
                    IoBuffer bbuf = (IoBuffer) readFuture.getMessage();
                    byte[] byten = new byte[bbuf.limit()];
                    bbuf.get(byten, bbuf.position(), bbuf.limit());
                    if (isHex) {
                        recvStr = ByteUtils.toHexString(byten);
                    } else {
                        recvStr = new String(byten, serverCharset);
                    }
                    log.debug("tcp receive:" + recvStr);
                }
            }
            return recvStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            doClose();
            return "";
        } finally {
            if (!keepAlive) {
                doCloseOnFlush();
            }
        }
    }

    /**
     * 异步发送报文
     *
     * @param socketHandle 响应报文处理类
     * @param mess         报文字符串
     */
    public void doSend(ISocketHandle socketHandle, final String mess) {
        doSend(socketHandle, mess, true);
    }

    /**
     * 异步发送报文
     *
     * @param socketHandle 响应报文处理类
     * @param mess         报文字符串
     * @param needRead     是否需要接收返回
     */
    public void doSend(ISocketHandle socketHandle, final String mess, boolean needRead) {
        this.socketHandle = socketHandle;
        try {
            if (connector == null || session == null || connector.isDisposed() || !session.isConnected() || connector.isDisposing() || session.isClosing()) {
                connector = new NioSocketConnector();
                connector.setHandler(this);
                setUpConfig();
                session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
                log.debug("connect tcp server[" + serverIp + ":port] timeOut:" + timeOut);
            }
            byte[] bts;
            if (isHex) {
                bts = ByteUtils.fromHexString(mess);
            } else {
                bts = mess.getBytes(serverCharset);
            }
            IoBuffer buffer = IoBuffer.allocate(bts.length);
            buffer.setAutoExpand(true);
            buffer.setAutoShrink(true);
            buffer.put(bts);
            buffer.flip();
            session.write(buffer);
            if (!needRead) {
                if (!keepAlive) {
                    doCloseOnFlush();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            doClose();
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String recvStr;
        if (isHex) {
            recvStr = ByteUtils.toHexString(byten);
        } else {
            recvStr = new String(byten, serverCharset);
        }
        log.debug("tcp receive:" + recvStr);
        if (socketHandle != null) {
            socketHandle.receiveMsg(recvStr);
        }
        if (!keepAlive) {
            if (session != null) {
                session.closeNow();
                connector = null;
            }
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
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        log.debug("tcp client session closed");
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception {
        super.sessionIdle(session, idlestatus);
        log.debug("tcp client session idle");
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    public String getServerCharset() {
        return serverCharset;
    }

    /**
     * 默认使用系统字符集
     *
     * @param serverCharset 字符集
     */
    public void setServerCharset(String serverCharset) {
        this.serverCharset = serverCharset;
    }

    /**
     * 是否十六进制
     *
     * @return 是否十六进制
     */
    public boolean isHex() {
        return isHex;
    }

    /**
     * 是否以十六进制进行通讯，默认false
     *
     * @param isHex 是否十六进制
     */
    public void setHex(boolean isHex) {
        this.isHex = isHex;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
}
