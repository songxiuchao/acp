package pers.acp.spring.boot.init

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.conf.AcpCoreConfiguration
import pers.acp.spring.boot.init.task.InitTcpServer
import pers.acp.spring.boot.init.task.InitUdpServer
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * 初始化系统及TCP、UDP服务
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class InitServer @Autowired
constructor(private val log: LogAdapter,
            private val initTcpServer: InitTcpServer,
            private val initUdpServer: InitUdpServer,
            private val acpCoreConfiguration: AcpCoreConfiguration) {

    /**
     * 主线程中进行系统初始化
     */
    internal fun start() {
        try {
            initTcpServer.startTcpServer()
            initUdpServer.startUdpServer()
            initTools()
        } catch (e: Exception) {
            log.error("system startup Exception:" + e.message)
        }

    }

    private fun initTools() {
        log.info("tools init begin ...")
        CommonTools.initTools(acpCoreConfiguration.deleteFileWaitTime,
                acpCoreConfiguration.absPathPrefix,
                acpCoreConfiguration.userPathPrefix,
                acpCoreConfiguration.fontPath)
        log.info("tools init finished!")
    }
}