package pers.acp.springboot.core.socket.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.springboot.core.socket.base.SockertServerHandle;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
public class TcpServerHandle extends SockertServerHandle {

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
