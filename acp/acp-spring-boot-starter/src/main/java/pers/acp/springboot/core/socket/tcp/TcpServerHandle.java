package pers.acp.springboot.core.socket.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.springboot.core.socket.base.BaseSocketHandle;
import pers.acp.springboot.core.socket.config.ListenConfig;
import pers.acp.core.log.LogFactory;

/**
 * 报文处理类
 *
 * @author zhang
 */
public final class TcpServerHandle implements Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private IoSession session;

    private ListenConfig listenConfig;

    private BaseSocketHandle socketResponse;

    private String recvStr;

    TcpServerHandle(IoSession session, ListenConfig listenConfig, BaseSocketHandle socketResponse, String recvStr) {
        super();
        this.session = session;
        this.listenConfig = listenConfig;
        this.socketResponse = socketResponse;
        this.recvStr = recvStr;
    }

    public void run() {
        String responseStr = this.socketResponse.doResponse(recvStr);
        try {
            byte[] bts;
            if (listenConfig.isHex()) {
                bts = ByteUtils.fromHexString(responseStr);
            } else {
                bts = responseStr.getBytes(listenConfig.getCharset());
            }
            IoBuffer buffer = IoBuffer.allocate(bts.length);
            buffer.setAutoExpand(true);
            buffer.setAutoShrink(true);
            buffer.put(bts);
            buffer.flip();
            if (listenConfig.isResponsable()) {
                session.write(buffer);
                log.debug("tcp return:" + responseStr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (session != null) {
                session.closeNow();
            }
        }
    }
}
