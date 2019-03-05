package pers.acp.client.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import pers.acp.client.socket.base.SockertClient;
import pers.acp.core.log.LogFactory;

import java.net.InetSocketAddress;

public final class UdpClient extends SockertClient {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 创建socket发送客户端
     *
     * @param serverIp 发送IP
     * @param port     发送端口
     * @param timeOut  超时时间
     */
    public UdpClient(String serverIp, int port, int timeOut) {
        super(serverIp, port, timeOut);
    }

    /**
     * 创建链接
     */
    public void connect() {
        group = new NioEventLoopGroup();
        try {
            UdpClient clientHandle = this;
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(clientHandle);
                        }
                    });
            this.channel = b.connect(serverIp, port).sync().channel();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            group.shutdownGracefully();
        }
    }

    @Override
    public Object beforeSendMessage(String sendStr) throws Exception {
        return new DatagramPacket(Unpooled.copiedBuffer(sendStr.getBytes(serverCharset)), new InetSocketAddress(serverIp, port));
    }

    @Override
    public void afterSendMessage(Channel channel) {
    }

    @Override
    public ByteBuf beforeReadMessage(Object msg) {
        DatagramPacket message = (DatagramPacket) msg;
        return message.content();
    }

    @Override
    public void afterReadMessage(ChannelHandlerContext ctx) {
        doClose();
    }

}
