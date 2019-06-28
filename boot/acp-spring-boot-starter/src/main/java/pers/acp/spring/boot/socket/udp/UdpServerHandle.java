package pers.acp.spring.boot.socket.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import pers.acp.spring.boot.socket.base.SocketServerHandle;
import pers.acp.spring.boot.conf.SocketListenerConfiguration;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;

/**
 * Udp 报文处理
 *
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
public class UdpServerHandle extends SocketServerHandle {

    UdpServerHandle(SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle) {
        super(socketListenerConfiguration, socketServerHandle);
    }

    @Override
    protected ByteBuf beforeReadMessage(Object msg) {
        DatagramPacket message = (DatagramPacket) msg;
        return message.content();
    }

    @Override
    protected void afterSendMessage(ChannelHandlerContext ctx) {
    }

    @Override
    protected void afterReadMessage(ChannelHandlerContext ctx) {
    }

    @Override
    protected Object beforeSendMessage(ChannelHandlerContext ctx, Object requestMsg, String sendStr) throws Exception {
        DatagramPacket packet = (DatagramPacket) requestMsg;
        return new DatagramPacket(Unpooled.copiedBuffer(sendStr.getBytes(socketListenerConfiguration.getCharset())), packet.sender());
    }

}
