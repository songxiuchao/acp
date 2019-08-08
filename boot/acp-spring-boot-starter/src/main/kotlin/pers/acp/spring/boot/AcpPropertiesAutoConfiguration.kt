package pers.acp.spring.boot

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.spring.boot.conf.*

/**
 * @author zhang by 13/07/2019
 * @since JDK 11
 */
@Configuration
@EnableConfigurationProperties(
        AcpCoreConfiguration::class,
        ControllerAspectConfiguration::class,
        ScheduleConfiguration::class,
        SwaggerConfiguration::class,
        TcpServerConfiguration::class,
        UdpServerConfiguration::class)
@AutoConfigureAfter(TaskSchedulingAutoConfiguration::class)
class AcpPropertiesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JacksonProperties::class)
    fun jacksonProperties() = JacksonProperties()

    @Bean
    @ConditionalOnMissingBean(TaskSchedulingProperties::class)
    fun taskSchedulingProperties() = TaskSchedulingProperties()

}