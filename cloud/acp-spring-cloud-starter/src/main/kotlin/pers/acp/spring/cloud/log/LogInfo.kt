package pers.acp.spring.cloud.log

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pers.acp.core.CommonTools

import java.net.InetAddress
import java.net.UnknownHostException

/**
 * 日志消息实体
 *
 * @author zhangbin by 11/07/2018 13:34
 * @since JDK 11
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class LogInfo {

    @Value("\${server.address}")
    private val serverIp: String? = null

    @Value("\${spring.application.name}")
    val serverName: String? = null

    @Value("\${server.port}")
    val serverPort: Int = 0

    /**
     * 日志类型字符串
     * 在 log-server 的 logback.xml 中对应配置日志规则，可实现不同类型的日志记录到的文件
     *
     * @see LogConstant.DEFAULT_TYPE
     * 默认日志类型为“ALL”，新增日志类型之后需在 log-server 中的 logback.xml 参照 ALL 进行配置
     */
    var logType = LogConstant.DEFAULT_TYPE

    var logLevel: String? = null
        internal set

    var serverTime: Long? = null

    var className: String? = null

    var lineno: Int = 0

    var message: String? = null
        internal set

    var throwable: Throwable? = null
        internal set

    var params: List<Any?> = listOf()
        internal set

    fun getServerIp(): String? {
        return if (CommonTools.isNullStr(serverIp)) {
            try {
                InetAddress.getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                ""
            }
        } else {
            serverIp
        }
    }

}
