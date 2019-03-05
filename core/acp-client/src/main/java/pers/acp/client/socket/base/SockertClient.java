package pers.acp.client.socket.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.nio.charset.Charset;

/**
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
public abstract class SockertClient extends ChannelInboundHandlerAdapter {

    protected final LogFactory log = LogFactory.getInstance(this.getClass());

    private final Object lock = new Object();

    protected int MAX_TIME = 3600000;

    protected String serverIp;

    protected int port;

    protected int timeOut;

    protected String serverCharset = CommonTools.getDefaultCharset();

    private boolean isHex = false;

    protected EventLoopGroup group = null;

    protected ByteToMessageDecoder messageDecoder = null;

    protected ISocketClientHandle socketHandle = null;

    protected Channel channel = null;

    public SockertClient(String serverIp, int port, int timeOut) {
        this.port = port;
        this.serverIp = serverIp;
        if (timeOut < MAX_TIME) {
            this.timeOut = timeOut;
        } else {
            this.timeOut = MAX_TIME;
        }
    }

    public void doSend(String requestStr) {
        try {
            synchronized (lock) {
                if (isClosed()) {
                    connect();
                }
            }
            String sendStr = requestStr;
            if (isHex) {
                byte[] bts = ByteBufUtil.decodeHexDump(sendStr);
                sendStr = new String(bts, serverCharset);
            }
            Object msgPackege = beforeSendMessage(sendStr);
            channel.writeAndFlush(msgPackege);
            afterSendMessage(channel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = beforeReadMessage(msg);
            String recvStr;
            if (isHex) {
                recvStr = ByteBufUtil.hexDump(byteBuf);
            } else {
                recvStr = byteBuf.toString(Charset.forName(serverCharset));
            }
            log.debug("socket receive:" + recvStr);
            if (socketHandle != null) {
                socketHandle.receiveMsg(recvStr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(msg);
            afterReadMessage(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    protected abstract void connect();

    protected abstract Object beforeSendMessage(String sendStr) throws Exception;

    protected abstract void afterSendMessage(Channel channel);

    protected abstract ByteBuf beforeReadMessage(Object msg);

    protected abstract void afterReadMessage(ChannelHandlerContext ctx);

    private boolean isClosed() {
        return channel == null || group == null || !channel.isOpen() || group.isShuttingDown() || group.isShutdown() || group.isTerminated();
    }

    public void doClose() {
        group.shutdownGracefully();
    }

    public void setHex(boolean hex) {
        isHex = hex;
    }

    public void setSocketHandle(ISocketClientHandle socketHandle) {
        this.socketHandle = socketHandle;
    }

    public void setMessageDecoder(ByteToMessageDecoder messageDecoder) {
        this.messageDecoder = messageDecoder;
    }

    public void setServerCharset(String serverCharset) {
        this.serverCharset = serverCharset;
    }

}
