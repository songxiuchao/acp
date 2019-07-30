package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy
import feign.RequestInterceptor
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pers.acp.core.CommonTools
import pers.acp.spring.cloud.component.FeignHystrixConcurrencyStrategy
import pers.acp.spring.cloud.error.AuthAccessDeniedHandler
import pers.acp.spring.cloud.error.AuthExceptionEntryPoint
import pers.acp.spring.cloud.log.LogInfo

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration
class AcpCloudComponentAutoConfiguration {

    /**
     * 自定义 Feign 请求拦截器，请求之前将 Oauth2 token 信息带入 Request 的 header 进行权限传递
     *
     * @return 自定义 Feign 请求拦截器实例
     */
    @Primary
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            // 获取当前服务的 request 对象，将 header 中的 Authorization 传递给 feign 的 request 对象
            RequestContextHolder.getRequestAttributes()?.let {
                if (it is ServletRequestAttributes) {
                    val request = it.request
                    val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
                    if (!CommonTools.isNullStr(authorization)) {
                        template.header(HttpHeaders.AUTHORIZATION, authorization)
                    }
                }
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(HystrixConcurrencyStrategy::class)
    fun feignHystrixConcurrencyStrategy() = FeignHystrixConcurrencyStrategy()

    @Bean
    fun authAccessDeniedHandler(objectMapper: ObjectMapper) = AuthAccessDeniedHandler(objectMapper)

    @Bean
    fun authExceptionEntryPoint(objectMapper: ObjectMapper) = AuthExceptionEntryPoint(objectMapper)

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun logInfo() = LogInfo()

}