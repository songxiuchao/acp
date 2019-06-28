package pers.acp.spring.boot.socket.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import pers.acp.core.CommonTools;
import pers.acp.spring.boot.socket.base.SocketServerHandle;
import pers.acp.spring.boot.conf.SocketListenerConfiguration;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Tcp 报文处理
 *
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
public class TcpServerHandle extends SocketServerHandle {

    TcpServerHandle(SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle) {
        super(socketListenerConfiguration, socketServerHandle);
    }

    @Override
    protected ByteBuf beforeReadMessage(Object msg) {
        return (ByteBuf) msg;
    }

    @Override
    protected void afterSendMessage(ChannelHandlerContext ctx) {
        if (!socketListenerConfiguration.isKeepAlive()) {
            ctx.close();
        }
    }

    @Override
    protected void afterReadMessage(ChannelHandlerContext ctx) {
        if (!socketListenerConfiguration.isKeepAlive()) {
            ctx.close();
        }
    }

    @Override
    protected Object beforeSendMessage(ChannelHandlerContext ctx, Object requestMsg, String sendStr) throws Exception {
        return ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(sendStr), Charset.forName(socketListenerConfiguration.getCharset()));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        String idelStr = socketServerHandle.userEventTriggered(event);
        if (!CommonTools.isNullStr(idelStr)) {
            doResponse(ctx, null, idelStr);
        }
    }

}
