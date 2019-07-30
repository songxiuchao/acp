package pers.acp.spring.boot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pers.acp.spring.boot.aspect.RestControllerAspect
import pers.acp.spring.boot.component.FileDownLoadHandle
import pers.acp.spring.boot.conf.ControllerAspectConfiguration
import pers.acp.spring.boot.exceptions.RestExceptionHandler
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.tools.PackageTools
import pers.acp.spring.boot.tools.SpringBeanFactory

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration
class AcpComponentAutoConfiguration {

    @Bean
    fun springBeanFactory() = SpringBeanFactory()

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    @ConditionalOnBean(JacksonProperties::class)
    fun jacksonObjectMapper(jacksonProperties: JacksonProperties): ObjectMapper =
            PackageTools.buildJacksonObjectMapper(jacksonProperties).apply {
                Class.forName("com.fasterxml.jackson.module.kotlin.KotlinModule")?.also {
                    this.registerKotlinModule()
                }
            }

    @Bean
    fun restControllerAspect(controllerAspectConfiguration: ControllerAspectConfiguration,
                             objectMapper: ObjectMapper,
                             logAdapter: LogAdapter) = RestControllerAspect(controllerAspectConfiguration, objectMapper, logAdapter)

    @Bean
    fun restExceptionHandler(logAdapter: LogAdapter) = RestExceptionHandler(logAdapter)

    @Bean
    fun fileDownLoadHandle(logAdapter: LogAdapter) = FileDownLoadHandle(logAdapter)

}