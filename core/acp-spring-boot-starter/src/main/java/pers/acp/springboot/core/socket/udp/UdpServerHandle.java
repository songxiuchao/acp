package pers.acp.springboot.core.socket.udp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;
import pers.acp.core.log.LogFactory;

/**
 * 报文处理类
 *
 * @author zhang
 */
public final class UdpServerHandle implements Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private IoSession session;

    private SocketListenerConfiguration socketListenerConfiguration;

    private ISocketServerHandle socketServerHandle;

    private String recvStr;

    UdpServerHandle(IoSession session, SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle, String recvStr) {
        super();
        this.session = session;
        this.socketListenerConfiguration = socketListenerConfiguration;
        this.socketServerHandle = socketServerHandle;
        this.recvStr = recvStr;
    }

    public void run() {
        String responseStr = this.socketServerHandle.doResponse(recvStr);
        if (socketListenerConfiguration.isResponsable()) {
            responseStr = CommonTools.isNullStr(responseStr) ? "" : responseStr;
            try {
                byte[] bts;
                if (socketListenerConfiguration.isHex()) {
                    bts = ByteUtils.fromHexString(responseStr);
                } else {
                    bts = responseStr.getBytes(socketListenerConfiguration.getCharset());
                }
                IoBuffer buffer = IoBuffer.allocate(bts.length);
                buffer.setAutoExpand(true);
                buffer.setAutoShrink(true);
                buffer.put(bts);
                buffer.flip();
                session.write(buffer);
                log.debug("udp return:" + responseStr);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (session != null) {
                    session.closeNow();
                }
            }
        }
    }
}
