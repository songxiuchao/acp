package pers.acp.spring.cloud.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.spring.cloud.log.consumer.DefaultLogProcess
import pers.acp.spring.cloud.log.consumer.LogConsumer
import pers.acp.spring.cloud.log.consumer.LogInput
import pers.acp.spring.cloud.log.consumer.LogProcess
import pers.acp.spring.cloud.conf.LogServerConfiguration

import javax.annotation.PostConstruct

/**
 * 日志服务自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration
@ConditionalOnExpression("'\${acp.cloud.log-server.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration::class)
@EnableBinding(LogInput::class)
class LogServerAutoConfiguration @Autowired
constructor(private val logServerConfiguration: LogServerConfiguration, private val bindings: BindingServiceProperties) {

    /**
     * 初始化日志消息消费者
     */
    @PostConstruct
    fun init() {
        if (logServerConfiguration.isEnabled) {
            if (this.bindings.bindings[LogConstant.INPUT] == null) {
                this.bindings.bindings[LogConstant.INPUT] = BindingProperties()
            }
            this.bindings.bindings[LogConstant.INPUT]?.let {
                if (it.destination == null || it.destination == LogConstant.INPUT) {
                    it.destination = logServerConfiguration.destination
                }
                it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
                it.group = logServerConfiguration.groupId
            }
        }
    }

    @Bean
    fun logConsumer(objectMapper: ObjectMapper, logProcess: LogProcess): LogConsumer {
        return LogConsumer(objectMapper, logProcess)
    }

    @Bean
    @ConditionalOnMissingBean(LogProcess::class)
    fun logProcess(): LogProcess = DefaultLogProcess()

}
