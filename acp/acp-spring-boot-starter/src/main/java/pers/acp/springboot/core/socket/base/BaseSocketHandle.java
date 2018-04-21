package pers.acp.springboot.core.socket.base;

public abstract class BaseSocketHandle {

    /**
     * 对接收到的报文进行处理
     *
     * @param recvStr 接收到的报文
     * @return 返回报文
     */
    public abstract String doResponse(String recvStr);

}
