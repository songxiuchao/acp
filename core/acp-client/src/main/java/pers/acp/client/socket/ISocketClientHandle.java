package pers.acp.client.socket;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Create by zhangbin on 2017-11-6 10:41
 */
public interface ISocketClientHandle {

    /**
     * 接收消息
     *
     * @param recvStr 接收的字符串
     */
    void receiveMsg(String recvStr);

    void exceptionCaught(IoSession session, Throwable cause) throws Exception;

    void sessionClosed(IoSession session) throws Exception;

    void sessionCreated(IoSession session) throws Exception;

    void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception;

    void sessionOpened(IoSession session) throws Exception;

}
