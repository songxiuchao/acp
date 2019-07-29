package pers.acp.spring.cloud.log

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.support.MessageBuilder
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.tools.SpringBeanFactory
import pers.acp.spring.cloud.enums.LogLevel
import pers.acp.spring.cloud.log.producer.LogProducer
import pers.acp.spring.cloud.conf.LogServerClientConfiguration

/**
 * 日志实例
 *
 * @author zhangbin by 11/07/2018 13:36
 * @since JDK 11
 */
class CloudLogAdapter(private val logServerClientConfiguration: LogServerClientConfiguration,
                      private val objectMapper: ObjectMapper) : LogAdapter {

    private val log = LogFactory.getInstance(this.javaClass, 4)

    private fun generateLogInfo(): LogInfo? {
        val logInfo = SpringBeanFactory.getBean(LogInfo::class.java)
        logInfo?.let {
            var logType = logServerClientConfiguration.logType
            if (CommonTools.isNullStr(logType)) {
                logType = LogConstant.DEFAULT_TYPE
            }
            it.logType = logType
        }
        return logInfo
    }

    private fun sendToLogServer(logInfo: LogInfo) {
        val stacks = Thread.currentThread().stackTrace
        GlobalScope.launch {
            logInfo.serverTime = CommonTools.getNowDateTime().toDate().time
            var lineno = 0
            var className = ""
            if (stacks.size >= log.stackIndex + 1) {
                lineno = stacks[log.stackIndex].lineNumber
                className = stacks[log.stackIndex].className
            }
            logInfo.lineno = lineno
            logInfo.className = className
            try {
                if (logServerClientConfiguration.enabled) {
                    val logProducer = SpringBeanFactory.getBean(LogProducer::class.java)
                    logProducer?.logOutput?.sendMessage()?.send(MessageBuilder.withPayload(objectMapper.writeValueAsString(logInfo)).build())
                }
            } catch (e: JsonProcessingException) {
                log.error(e.message, e)
            }
        }
    }

    override fun info(message: String?) {
        log.info(message)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Info.name
            it.message = message
            sendToLogServer(it)
        }
    }

    override fun info(message: String?, vararg variable: Any?) {
        log.info(message, *variable)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Info.name
            it.message = message
            it.params = arrayListOf(*variable)
            sendToLogServer(it)
        }
    }

    override fun info(message: String?, t: Throwable?) {
        log.info(message, t)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Info.name
            it.message = message
            it.throwable = t
            sendToLogServer(it)
        }
    }

    override fun debug(message: String?) {
        log.debug(message)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Debug.name
            it.message = message
            sendToLogServer(it)
        }
    }

    override fun debug(message: String?, vararg variable: Any?) {
        log.debug(message, *variable)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Debug.name
            it.message = message
            it.params = arrayListOf(*variable)
            sendToLogServer(it)
        }
    }

    override fun debug(message: String?, t: Throwable?) {
        log.debug(message, t)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Debug.name
            it.message = message
            it.throwable = t
            sendToLogServer(it)
        }
    }

    override fun warn(message: String?) {
        log.warn(message)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Warn.name
            it.message = message
            sendToLogServer(it)
        }
    }

    override fun warn(message: String?, vararg variable: Any?) {
        log.warn(message, *variable)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Warn.name
            it.message = message
            it.params = arrayListOf(*variable)
            sendToLogServer(it)
        }
    }

    override fun warn(message: String?, t: Throwable?) {
        log.warn(message, t)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Warn.name
            it.message = message
            it.throwable = t
            sendToLogServer(it)
        }
    }

    override fun error(message: String?) {
        log.error(message)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Error.name
            it.message = message
            sendToLogServer(it)
        }
    }

    override fun error(message: String?, vararg variable: Any?) {
        log.error(message, *variable)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Error.name
            it.message = message
            it.params = arrayListOf(*variable)
            sendToLogServer(it)
        }
    }

    override fun error(message: String?, t: Throwable?) {
        log.error(message, t)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Error.name
            it.message = message
            it.throwable = t
            sendToLogServer(it)
        }
    }

    override fun trace(message: String?) {
        log.trace(message)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Trace.name
            it.message = message
            sendToLogServer(it)
        }
    }

    override fun trace(message: String?, vararg variable: Any?) {
        log.trace(message, *variable)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Trace.name
            it.message = message
            it.params = arrayListOf(*variable)
            sendToLogServer(it)
        }
    }

    override fun trace(message: String?, t: Throwable?) {
        log.trace(message, t)
        val logInfo = generateLogInfo()
        logInfo?.let {
            it.logLevel = LogLevel.Trace.name
            it.message = message
            it.throwable = t
            sendToLogServer(it)
        }
    }

}
