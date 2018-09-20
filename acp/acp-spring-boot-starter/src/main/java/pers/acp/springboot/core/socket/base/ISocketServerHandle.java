package pers.acp.springboot.core.socket.base;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface ISocketServerHandle {

    /**
     * 对接收到的报文进行处理
     *
     * @param recvStr 接收到的报文
     * @return 返回报文
     */
    String doResponse(String recvStr);

    void exceptionCaught(IoSession session, Throwable cause) throws Exception;

    void sessionClosed(IoSession session) throws Exception;

    void sessionCreated(IoSession session) throws Exception;

    void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception;

    void sessionOpened(IoSession session) throws Exception;

}
