package pers.acp.spring.boot.socket.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import pers.acp.core.interfaces.IDaemonService;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;
import pers.acp.spring.boot.conf.SocketListenerConfiguration;
import pers.acp.core.log.LogFactory;

import java.util.concurrent.TimeUnit;

public final class TcpServer implements IDaemonService, Runnable {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final int port;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workerGroup;

    private final SocketListenerConfiguration socketListenerConfiguration;

    private final ISocketServerHandle socketServerHandle;

    private final ByteToMessageDecoder messageDecoder;

    /**
     * 构造函数
     *
     * @param port                        端口
     * @param socketListenerConfiguration 监听服务配置
     * @param socketServerHandle          接收报文处理对象
     */
    public TcpServer(int port, SocketListenerConfiguration socketListenerConfiguration, ISocketServerHandle socketServerHandle, ByteToMessageDecoder messageDecoder) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup(socketListenerConfiguration.getThreadNumber());
        this.socketListenerConfiguration = socketListenerConfiguration;
        this.socketServerHandle = socketServerHandle;
        this.messageDecoder = messageDecoder;
    }

    @Override
    public void run() {
        if (this.socketServerHandle != null) {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, socketListenerConfiguration.isKeepAlive())
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            if (messageDecoder != null) {
                                ch.pipeline().addLast(messageDecoder);
                            }
                            if (socketListenerConfiguration.isKeepAlive()) {
                                ch.pipeline().addLast(new IdleStateHandler(socketListenerConfiguration.getIdletime(), socketListenerConfiguration.getIdletime(), socketListenerConfiguration.getIdletime(), TimeUnit.MILLISECONDS));
                            }
                            ch.pipeline().addLast(new TcpServerHandle(socketListenerConfiguration, socketServerHandle));
                        }
                    });
            try {
                //绑定端口，同步等待成功
                ChannelFuture f = bootstrap.bind(port).sync();
                //等待服务器监听端口关闭
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.error("tcp listen server is stop,case by:response object is null[BaseSocketHandle]");
        }
    }

    @Override
    public String getServiceName() {
        return socketListenerConfiguration.getName();
    }

    @Override
    public void stopService() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
