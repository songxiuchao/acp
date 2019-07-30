package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.component.FeignHystrixConcurrencyStrategy
import pers.acp.spring.cloud.conf.*
import pers.acp.spring.cloud.error.AuthAccessDeniedHandler
import pers.acp.spring.cloud.error.AuthExceptionEntryPoint
import pers.acp.spring.cloud.log.LogInfo

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration
@EnableConfigurationProperties(
        AcoCloudLogServerConfiguration::class,
        AcpCloudLogServerClientConfiguration::class,
        AcpCloudOauthConfiguration::class)
@ComponentScan(basePackageClasses = [
    AcpCloudFeignOauthRequestInterceptorConfiguration::class,
    AcpCloudResourceServerConfiguration::class])
class AcpCloudAutoConfiguration {

    @Bean
    fun feignHystrixConcurrencyStrategy() = FeignHystrixConcurrencyStrategy()

    @Bean
    fun authAccessDeniedHandler(objectMapper: ObjectMapper) = AuthAccessDeniedHandler(objectMapper)

    @Bean
    fun authExceptionEntryPoint(objectMapper: ObjectMapper) = AuthExceptionEntryPoint(objectMapper)

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun logInfo() = LogInfo()

}
