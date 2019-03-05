package pers.acp.client.socket.tcp;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import pers.acp.client.socket.base.SockertClient;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

public final class TcpClient extends SockertClient {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private int idleTime;

    private boolean keepAlive = false;

    private boolean needRead = true;

    /**
     * 创建socket发送客户端
     *
     * @param serverIp 发送IP
     * @param port     发送端口
     * @param timeOut  超时时间
     * @param idleTime 空闲等待时间
     */
    public TcpClient(String serverIp, int port, int timeOut, int idleTime) {
        super(serverIp, port, timeOut);
        if (idleTime < MAX_TIME) {
            this.idleTime = idleTime;
        } else {
            this.idleTime = MAX_TIME;
        }
    }

    /**
     * 创建链接
     */
    public void connect() {
        group = new NioEventLoopGroup();
        try {
            TcpClient clientHandle = this;
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            if (messageDecoder != null) {
                                ch.pipeline().addLast(messageDecoder);
                            }
                            ch.pipeline().addLast(new IdleStateHandler(idleTime, idleTime, idleTime, TimeUnit.MILLISECONDS), clientHandle);
                        }
                    });
            if (keepAlive) {
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            }
            this.channel = bootstrap.connect(serverIp, port).sync().channel();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            group.shutdownGracefully();
        }
    }

    @Override
    public Object beforeSendMessage(String sendStr) {
        return ByteBufUtil.encodeString(channel.alloc(), CharBuffer.wrap(sendStr), Charset.forName(serverCharset));
    }

    @Override
    public void afterSendMessage(Channel channel) {
        if (!keepAlive && !needRead) {
            channel.close();
            doClose();
        }
    }

    @Override
    public ByteBuf beforeReadMessage(Object msg) {
        return (ByteBuf) msg;
    }

    @Override
    public void afterReadMessage(ChannelHandlerContext ctx) {
        if (!keepAlive) {
            ctx.close();
            doClose();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (socketHandle != null) {
            String sendStr = socketHandle.userEventTriggered(event);
            if (!CommonTools.isNullStr(sendStr)) {
                doSend(sendStr);
            }
        }
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setNeedRead(boolean needRead) {
        this.needRead = needRead;
    }

}
