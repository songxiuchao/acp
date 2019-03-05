package pers.acp.client.socket.base;

import io.netty.handler.timeout.IdleStateEvent;

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

    /**
     * 事件触发
     *
     * @param evt 事件
     *            evt.state() IdleState
     * @return 发送内容
     * @throws Exception 异常
     */
    String userEventTriggered(IdleStateEvent evt) throws Exception;

}
