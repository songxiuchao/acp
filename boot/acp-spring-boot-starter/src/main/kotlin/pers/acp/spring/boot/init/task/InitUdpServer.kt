package pers.acp.spring.boot.init.task

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.conf.UdpServerConfiguration
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.init.BaseInitTask
import pers.acp.spring.boot.socket.udp.UdpServer
import pers.acp.spring.boot.socket.base.ISocketServerHandle

/**
 * 初始化UDP服务
 */
@Component
class InitUdpServer @Autowired(required = false)
constructor(private val udpServerConfiguration: UdpServerConfiguration, private val socketServerHandleList: List<ISocketServerHandle>) : BaseInitTask() {

    private val log = LogFactory.getInstance(this.javaClass)// 日志对象

    fun startUdpServer() {
        log.info("start udp listen service ...")
        if (socketServerHandleList.isNotEmpty()) {
            socketServerHandleList.forEach { socketServerHandle -> addServerHandle(socketServerHandle) }
        }
        try {
            val listens = udpServerConfiguration.listeners
            if (listens.isNotEmpty()) {
                listens.forEach { listen ->
                    run {
                        if (listen.enabled) {
                            val beanName = listen.handleBean
                            if (!CommonTools.isNullStr(beanName)) {
                                val handle = getSocketServerHandle(beanName)
                                if (handle != null) {
                                    val port = listen.port
                                    val server = UdpServer(port, listen, handle)
                                    val sub = Thread(server)
                                    sub.isDaemon = true
                                    sub.start()
                                    DaemonServiceManager.addService(server)
                                    log.info("start udp listen service Success [" + listen.name + "] , port:" + listen.port)
                                } else {
                                    log.error("udp handle bean [$beanName] is invalid!")
                                }
                            }
                        } else {
                            log.info("udp listen service is disabled [" + listen.name + "]")
                        }
                    }
                }
            } else {
                log.info("No udp listen service was found")
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            log.info("start udp listen service finished!")
        }
    }

}
