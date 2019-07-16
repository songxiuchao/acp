package pers.acp.spring.boot.init

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.spring.boot.component.SystemControl
import pers.acp.spring.boot.daemon.DaemonServiceManager

/**
 * 系统初始化
 * Created by zhangbin on 2017-6-16.
 */
@Component
class SystemInitialization @Autowired
constructor(private val systemControl: SystemControl, private val initServer: InitServer) : BaseInitialization() {

    private val log = LogFactory.getInstance(this.javaClass)// 日志对象

    override val name: String
        get() = "System Initialization"

    override val order: Int
        get() = 0

    override fun start() {
        log.info(">>>>>>>>>>>>>>>>>>>> system is starting ...")
        /* 启动初始化服务 */
        initServer.start()
        /* 启动 listener 及定时任务 */
        systemControl.initialization()
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { DaemonServiceManager.stopAllService() }))
        log.info(">>>>>>>>>>>>>>>>>>>> system start finished!")
    }

    override fun stop() {
        systemControl.stop()
    }
}
