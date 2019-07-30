package pers.acp.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class LogFactory {

    private var logger: Logger

    private var isCls: Boolean

    val stackIndex: Int

    private constructor(cls: Class<*>, stackIndex: Int = 3) {
        logger = LoggerFactory.getLogger(cls)
        this.stackIndex = stackIndex
        isCls = true
    }

    private constructor(name: String, stackIndex: Int = 3) {
        logger = LoggerFactory.getLogger(name)
        this.stackIndex = stackIndex
        isCls = false
    }

    fun info(message: String?) {
        setCustomerParams()
        logger.info(message)
    }

    fun info(message: String?, vararg variable: Any?) {
        setCustomerParams()
        logger.info(message, *variable)
    }

    fun info(message: String?, t: Throwable?) {
        setCustomerParams()
        logger.info(message, t)
    }

    fun debug(message: String?) {
        setCustomerParams()
        logger.debug(message)
    }

    fun debug(message: String?, vararg variable: Any?) {
        setCustomerParams()
        logger.debug(message, *variable)
    }

    fun debug(message: String?, t: Throwable?) {
        setCustomerParams()
        logger.debug(message, t)
    }

    fun warn(message: String?) {
        setCustomerParams()
        logger.warn(message)
    }

    fun warn(message: String?, vararg variable: Any?) {
        setCustomerParams()
        logger.warn(message, *variable)
    }

    fun warn(message: String?, t: Throwable?) {
        setCustomerParams()
        logger.warn(message, t)
    }

    fun error(message: String?) {
        setCustomerParams()
        logger.error(message)
    }

    fun error(message: String?, vararg variable: Any?) {
        setCustomerParams()
        logger.debug(message, *variable)
    }

    fun error(message: String?, t: Throwable?) {
        setCustomerParams()
        logger.error(message, t)
    }

    fun trace(message: String?) {
        setCustomerParams()
        logger.trace(message)
    }

    fun trace(message: String?, vararg variable: Any?) {
        setCustomerParams()
        logger.trace(message, *variable)
    }

    fun trace(message: String?, t: Throwable?) {
        setCustomerParams()
        logger.trace(message, t)
    }

    private fun setCustomerParams() {
        val stacks = Thread.currentThread().stackTrace
        var lineno = 0
        var className = ""
        if (stacks.size >= stackIndex + 1) {
            lineno = stacks[stackIndex].lineNumber
            className = stacks[stackIndex].className
        }
        if (isCls) {
            logger = LoggerFactory.getLogger(className)
        }
        MDC.put("lineno", lineno.toString())
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun getInstance(cls: Class<*>, stackIndex: Int = 3): LogFactory {
            return LogFactory(cls, stackIndex)
        }

        @JvmStatic
        @JvmOverloads
        fun getInstance(name: String, stackIndex: Int = 3): LogFactory {
            return LogFactory(name, stackIndex)
        }
    }

}
