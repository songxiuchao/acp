package pers.acp.spring.boot

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pers.acp.spring.boot.tools.PackageTools

/**
 * @author zhang by 13/07/2019
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.spring.boot")
class AcpAutoConfiguration {

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    @ConditionalOnBean(JacksonProperties::class)
    fun jacksonObjectMapper(jacksonProperties: JacksonProperties): ObjectMapper = PackageTools.buildJacksonObjectMapper(jacksonProperties)

    @Bean
    @ConditionalOnMissingBean(JacksonProperties::class)
    fun jacksonProperties() = JacksonProperties()

    @Bean
    @ConditionalOnMissingBean(TaskSchedulingProperties::class)
    fun taskSchedulingProperties() = TaskSchedulingProperties()

}