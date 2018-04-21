package pers.acp.client.socket;

/**
 * Create by zhangbin on 2017-11-6 10:41
 */
public interface ISocketHandle {

    /**
     * 接收消息
     *
     * @param recvStr 接收的字符串
     */
    void receiveMsg(String recvStr);

}
