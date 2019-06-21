package pers.acp.springboot.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.conf.SocketListenerConfiguration;
import pers.acp.springboot.socket.base.ISocketServerHandle;

public final class UdpServer implements IDaemonService, Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private int port;

    private EventLoopGroup bossGroup;

    private SocketListenerConfiguration socketListenerConfiguration;

    private ISocketServerHandle socketServerHandle;

    /**
     * 构造函数
     *
     * @param port                        端口
     * @param socketListenerConfiguration 监听服务配置
     * @param socketServerHandle          接收报文处理对象
     */
    public UdpServer(int port, SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.socketListenerConfiguration = socketListenerConfiguration;
        this.socketServerHandle = socketServerHandle;
    }

    @Override
    public void run() {
        if (socketServerHandle != null) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new UdpServerHandle(socketListenerConfiguration, socketServerHandle));
                        }
                    });
            try {
                bootstrap.bind(port).sync().channel().closeFuture().sync();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.error("udp listen server is stop,case by:response object is null[BaseSocketHandle]");
        }
    }

    @Override
    public String getServiceName() {
        return socketListenerConfiguration.getName();
    }

    @Override
    public void stopService() {
        bossGroup.shutdownGracefully();
    }

}
