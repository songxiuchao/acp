package pers.acp.springboot.core.socket.base;

import io.netty.handler.timeout.IdleStateEvent;

public interface ISocketServerHandle {

    /**
     * 对接收到的报文进行处理
     *
     * @param recvStr 接收到的报文
     * @return 返回报文
     */
    String doResponse(String recvStr);

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
