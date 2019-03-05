package pers.acp.springboot.core.socket.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.conf.SocketListenerConfiguration;

import java.nio.charset.Charset;

/**
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
public abstract class SockertServerHandle extends ChannelInboundHandlerAdapter {

    protected final LogFactory log = LogFactory.getInstance(this.getClass());

    protected SocketListenerConfiguration socketListenerConfiguration;

    protected ISocketServerHandle socketServerHandle;

    public SockertServerHandle(SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle) {
        this.socketListenerConfiguration = socketListenerConfiguration;
        this.socketServerHandle = socketServerHandle;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = beforeReadMessage(msg);
            String recvStr;
            if (socketListenerConfiguration.isHex()) {
                recvStr = ByteBufUtil.hexDump(byteBuf);
            } else {
                recvStr = byteBuf.toString(Charset.forName(socketListenerConfiguration.getCharset()));
            }
            log.debug("socket receive:" + recvStr);
            String responseStr = this.socketServerHandle.doResponse(recvStr);
            if (socketListenerConfiguration.isResponsable()) {
                responseStr = CommonTools.isNullStr(responseStr) ? "" : responseStr;
                try {
                    doResponse(ctx, msg, responseStr);
                    log.debug("socket return:" + responseStr);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(msg);
            afterReadMessage(ctx);
        }
    }

    protected void doResponse(ChannelHandlerContext ctx, Object requestMsg, String responseStr) throws Exception {
        String retStr = responseStr;
        if (socketListenerConfiguration.isHex()) {
            byte[] bts = ByteBufUtil.decodeHexDump(responseStr);
            retStr = new String(bts, socketListenerConfiguration.getCharset());
        }
        Object msgPackege = beforeSendMessage(ctx, requestMsg, retStr);
        ctx.writeAndFlush(msgPackege);
        afterSendMessage(ctx);
    }

    protected abstract ByteBuf beforeReadMessage(Object msg);

    protected abstract void afterReadMessage(ChannelHandlerContext ctx);

    protected abstract Object beforeSendMessage(ChannelHandlerContext ctx, Object requestMsg, String sendStr) throws Exception;

    protected abstract void afterSendMessage(ChannelHandlerContext ctx);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

}
