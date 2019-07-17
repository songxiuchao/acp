package pers.acp.spring.boot.socket.tcp

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.timeout.IdleStateHandler
import pers.acp.core.interfaces.IDaemonService
import pers.acp.spring.boot.socket.base.ISocketServerHandle
import pers.acp.spring.boot.conf.SocketListenerConfiguration
import pers.acp.core.log.LogFactory

import java.util.concurrent.TimeUnit

/**
 * Tcp服务端
 */
class TcpServer
/**
 * 构造函数
 *
 * @param port                        端口
 * @param socketListenerConfiguration 监听服务配置
 * @param socketServerHandle          接收报文处理对象
 */
(private val port: Int, private val socketListenerConfiguration: SocketListenerConfiguration, private val socketServerHandle: ISocketServerHandle?, private val messageDecoder: ByteToMessageDecoder?) : IDaemonService, Runnable {

    private val log = LogFactory.getInstance(this.javaClass)

    private val bossGroup: EventLoopGroup

    private val workerGroup: EventLoopGroup

    init {
        this.bossGroup = NioEventLoopGroup()
        this.workerGroup = NioEventLoopGroup(socketListenerConfiguration.threadNumber)
    }

    override fun run() {
        if (this.socketServerHandle != null) {
            try {
                //绑定端口，同步等待成功
                ServerBootstrap().group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel::class.java)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, socketListenerConfiguration.keepAlive)
                        .childHandler(object : ChannelInitializer<Channel>() {
                            override fun initChannel(ch: Channel) {
                                ch.pipeline().apply {
                                    messageDecoder?.let {
                                        this.addLast(it)
                                    }
                                    if (socketListenerConfiguration.keepAlive) {
                                        this.addLast(IdleStateHandler(socketListenerConfiguration.idletime, socketListenerConfiguration.idletime, socketListenerConfiguration.idletime, TimeUnit.MILLISECONDS))
                                    }
                                }.addLast(TcpServerHandle(socketListenerConfiguration, socketServerHandle))
                            }
                        }).bind(port).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        } else {
            log.error("tcp listen server is stop,case by:response object is null[BaseSocketHandle]")
        }
    }

    override fun getServiceName(): String = socketListenerConfiguration.name

    override fun stopService() {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }

}
