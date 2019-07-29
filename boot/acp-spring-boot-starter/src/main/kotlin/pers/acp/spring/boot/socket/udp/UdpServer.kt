package pers.acp.spring.boot.socket.udp

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.conf.SocketListenerConfiguration
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.socket.base.ISocketServerHandle

/**
 * Udp 服务端
 */
class UdpServer
/**
 * 构造函数
 *
 * @param log                         日志适配器
 * @param port                        端口
 * @param socketListenerConfiguration 监听服务配置
 * @param socketServerHandle          接收报文处理对象
 */
(private val log: LogAdapter,
 private val port: Int,
 private val socketListenerConfiguration: SocketListenerConfiguration,
 private val socketServerHandle: ISocketServerHandle?) : IDaemonService, Runnable {

    private val bossGroup: EventLoopGroup

    init {
        this.bossGroup = NioEventLoopGroup()
    }

    override fun run() {
        if (socketServerHandle != null) {
            try {
                Bootstrap().group(bossGroup)
                        .channel(NioDatagramChannel::class.java)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(object : ChannelInitializer<NioDatagramChannel>() {
                            override fun initChannel(ch: NioDatagramChannel) {
                                ch.pipeline().addLast(UdpServerHandle(log, socketListenerConfiguration, socketServerHandle))
                            }
                        }).bind(port).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                log.error(e.message, e)
            }

        } else {
            log.error("udp listen server is stop,case by:response object is null[BaseSocketHandle]")
        }
    }

    override fun getServiceName(): String = socketListenerConfiguration.name

    override fun stopService() {
        bossGroup.shutdownGracefully()
    }

}
