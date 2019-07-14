package pers.acp.spring.cloud.log.producer

import org.springframework.beans.factory.annotation.Autowired

/**
 * @author zhang by 14/01/2019 17:08
 * @since JDK 11
 */
class LogProducer @Autowired
constructor(val logOutput: LogOutput)
