package pers.acp.spring.boot.init.task

import io.netty.handler.codec.ByteToMessageDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.conf.TcpServerConfiguration
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.init.BaseInitTask
import pers.acp.spring.boot.socket.tcp.TcpServer
import pers.acp.spring.boot.socket.base.ISocketServerHandle

/**
 * 初始化TCP服务
 */
@Component
class InitTcpServer @Autowired(required = false)
constructor(private val tcpServerConfiguration: TcpServerConfiguration, private val socketServerHandleList: List<ISocketServerHandle>, private val byteToMessageDecoderList: List<ByteToMessageDecoder>) : BaseInitTask() {

    private val log = LogFactory.getInstance(this.javaClass)// 日志对象

    fun startTcpServer() {
        log.info("start tcp listen service ...")
        if (socketServerHandleList.isNotEmpty()) {
            socketServerHandleList.forEach { socketServerHandle -> addServerHandle(socketServerHandle) }
        }
        if (byteToMessageDecoderList.isNotEmpty()) {
            byteToMessageDecoderList.forEach { byteToMessageDecoder -> addMessageDecoder(byteToMessageDecoder) }
        }
        try {
            val listens = tcpServerConfiguration.listeners
            if (listens.isNotEmpty()) {
                listens.forEach { listen ->
                    run {
                        if (listen.enabled) {
                            val beanName = listen.handleBean
                            if (!CommonTools.isNullStr(beanName)) {
                                val handle = getSocketServerHandle(beanName)
                                if (handle != null) {
                                    val port = listen.port
                                    val tcpServer = TcpServer(port, listen, handle, getMessageDecoder(listen.messageDecoder))
                                    val thread = Thread(tcpServer)
                                    thread.isDaemon = true
                                    thread.start()
                                    DaemonServiceManager.addService(tcpServer)
                                    log.info("start tcp listen service Success [" + listen.name + "] , port:" + listen.port)
                                } else {
                                    log.error("tcp handle bean [$beanName] is invalid!")
                                }
                            }
                        } else {
                            log.info("tcp listen service is disabled [" + listen.name + "]")
                        }
                    }
                }
            } else {
                log.info("No tcp listen service was found")
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            log.info("start tcp listen service finished!")
        }
    }

}
