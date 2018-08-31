package pers.acp.client.socket.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.client.socket.ISocketClientHandle;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

public final class UdpClient extends IoHandlerAdapter {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final Object lock = new Object();

    private String serverIp;

    private int port;

    private int timeOut;

    private String serverCharset = CommonTools.getDefaultCharset();

    private boolean isHex = false;

    private ISocketClientHandle socketHandle = null;

    private IoConnector connector = null;

    private IoSession session = null;

    /**
     * 创建socket发送客户端
     *
     * @param serverIp 发送IP
     * @param port     发送端口
     * @param timeOut  接收超时时间
     */
    public UdpClient(String serverIp, int port, int timeOut) {
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
     * 该链接是否关闭
     *
     * @return boolean
     */
    public boolean isClosed() {
        return connector == null || session == null || connector.isDisposed() || !session.isConnected() || connector.isDisposing() || session.isClosing();
    }

    /**
     * 手动关闭连接
     */
    public void doDestroy() {
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
    public void doDestroyOnFlush() {
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
    }

    /**
     * 同步发送报文
     *
     * @param mess     报文字符串
     * @param needRead 是否需要接收返回信息
     * @return 响应报文
     */
    public String doSendSync(final String mess, boolean needRead) {
        try {
            synchronized (lock) {
                if (isClosed()) {
                    connector = new NioDatagramConnector();
                    connector.getSessionConfig().setUseReadOperation(true);
                    setUpConfig();
                    session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
                    log.debug("connect udp server[" + serverIp + ":port] timeOut:" + timeOut);
                }
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
            log.debug("udp send:" + mess);
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
                    log.debug("udp receive:" + recvStr);
                }
            }
            return recvStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            doDestroy();
        }
    }

    /**
     * 异步发送报文
     *
     * @param mess     报文字符串
     * @param needRead 是否需要接收返回信息
     */
    public void doSendAsync(final String mess, boolean needRead) {
        try {
            synchronized (lock) {
                if (isClosed()) {
                    connector = new NioDatagramConnector();
                    connector.setHandler(this);
                    setUpConfig();
                    session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
                    log.debug("connect udp server[" + serverIp + ":port] timeOut:" + timeOut);
                }
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
                doDestroyOnFlush();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            doDestroy();
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
        log.debug("udp receive:" + recvStr);
        if (socketHandle != null) {
            socketHandle.receiveMsg(recvStr);
        }
        doDestroy();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        if (session != null) {
            session.closeNow();
        }
        doDestroy();
        if (socketHandle != null) {
            socketHandle.exceptionCaught(session, cause);
        } else {
            super.exceptionCaught(session, cause);
        }
    }

    @Override
    public void messageSent(IoSession session, Object obj) throws Exception {
        super.messageSent(session, obj);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        log.debug("udp client session closed");
        if (session != null) {
            session.closeNow();
        }
        doDestroy();
        if (socketHandle != null) {
            socketHandle.sessionClosed(session);
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        if (socketHandle != null) {
            socketHandle.sessionCreated(session);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception {
        super.sessionIdle(session, idlestatus);
        log.debug("udp client session idle");
        if (session != null) {
            session.closeNow();
        }
        doDestroy();
        socketHandle.sessionIdle(session, idlestatus);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        if (socketHandle != null) {
            socketHandle.sessionOpened(session);
        }
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

    public ISocketClientHandle getSocketHandle() {
        return socketHandle;
    }

    public void setSocketHandle(ISocketClientHandle socketHandle) {
        this.socketHandle = socketHandle;
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
}
