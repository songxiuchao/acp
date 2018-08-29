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
import pers.acp.client.socket.ISocketHandle;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

public final class UdpClient extends IoHandlerAdapter {

    private LogFactory log = LogFactory.getInstance(this.getClass());

    private String serverIp;

    private int port;

    private int timeOut;

    private String serverCharset = CommonTools.getDefaultCharset();

    private boolean isHex = false;

    private ISocketHandle socketHandle = null;

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
     * 发送报文
     *
     * @param mess     报文字符串
     * @param needRead 是否需要接收返回信息
     * @return 响应报文
     */
    public String doSend(final String mess, boolean needRead) {
        IoConnector connector = null;
        IoSession session = null;
        try {
            connector = new NioDatagramConnector();
            connector.setConnectTimeoutMillis(timeOut);
            connector.getSessionConfig().setUseReadOperation(true);
            session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
            log.debug("connect udp server[" + serverIp + ":port] timeOut:" + timeOut);
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
            if (session != null) {
                session.closeNow();
            }
            if (connector != null) {
                connector.dispose();
            }
        }
    }

    /**
     * 发送报文
     *
     * @param socketHandle 响应报文处理类
     * @param mess         报文字符串
     * @param needRead     是否需要接收返回信息
     */
    public void doSend(ISocketHandle socketHandle, final String mess, boolean needRead) {
        this.socketHandle = socketHandle;
        IoConnector connector = null;
        IoSession session = null;
        try {
            connector = new NioDatagramConnector();
            connector.setConnectTimeoutMillis(timeOut);
            connector.setHandler(this);
            session = connector.connect(new InetSocketAddress(serverIp, port)).awaitUninterruptibly().getSession();
            log.debug("connect udp server[" + serverIp + ":port] timeOut:" + timeOut);
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
                session.closeOnFlush();
                connector.dispose(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (session != null) {
                session.closeNow();
            }
            if (connector != null) {
                connector.dispose();
            }
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
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setWriteTimeout(timeOut / 1000);
        cfg.setSoLinger(0);
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
}
