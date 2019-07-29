package pers.acp.spring.cloud.exceptions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ControllerAdvice
import pers.acp.spring.boot.constant.BootConfigurationOrder
import pers.acp.spring.boot.exceptions.RestExceptionHandler
import pers.acp.spring.cloud.log.LogInstance

/**
 * Create by zhangbin on 2017-8-10 16:26
 */
@Order(BootConfigurationOrder.restExceptionHandlerOrder - 1)
@ControllerAdvice
class CloudRestExceptionHandler @Autowired
constructor(private val logInstance: LogInstance) : RestExceptionHandler() {

    override fun doLog(ex: Throwable) {
        logInstance.error(ex.message, ex)
    }

}
