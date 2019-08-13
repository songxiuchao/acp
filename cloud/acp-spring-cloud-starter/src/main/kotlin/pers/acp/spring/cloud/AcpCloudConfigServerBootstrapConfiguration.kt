package pers.acp.spring.cloud

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import pers.acp.client.http.HttpClientBuilder
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.tools.PackageTools

import org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION
import org.springframework.cloud.config.client.ConfigServiceBootstrapConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

/**
 * 配置中心客户端自动配置
 * 装载优先级最高
 * 根据参数创建自定义客户端实例
 *
 * @author zhang by 21/03/2019
 * @since JDK 11
 */
@Configuration
@Import(ConfigServiceBootstrapConfiguration::class)
@EnableConfigurationProperties
@ConditionalOnClass(ConfigClientProperties::class)
class AcpCloudConfigServerBootstrapConfiguration @Autowired
constructor(private val environment: ConfigurableEnvironment) {

    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 加载配置中心自定义客户端
     *
     * @return ConfigServicePropertySourceLocator
     */
    @Primary
    @Bean
    @ConditionalOnProperty(value = ["spring.cloud.config.enabled"], matchIfMissing = true)
    fun acpConfigServicePropertySourceLocator(clientProperties: ConfigClientProperties): ConfigServicePropertySourceLocator {
        val locator = ConfigServicePropertySourceLocator(clientProperties)
        locator.setRestTemplate(customerConfigClientRestTemplate(clientProperties))
        log.info("Start Up Cloud, Configuration ConfigServicePropertySourceLocator For ACP")
        return locator
    }

    /**
     * 创建自定义 RestTemplate 客户端
     *
     * @param clientProperties 配置中心客户端配置内容
     * @return RestTemplate
     */
    private fun customerConfigClientRestTemplate(clientProperties: ConfigClientProperties): RestTemplate? {
        try {
            val requestFactory = OkHttp3ClientHttpRequestFactory(
                    HttpClientBuilder().maxTotalConn(environment.getProperty("feign.httpclient.max-connections", "1000").toInt())
                            .timeOut(environment.getProperty("feign.httpclient.connection-timeout", "10000").toInt())
                            .timeToLive(environment.getProperty("feign.httpclient.time-to-live", "900").toLong())
                            .timeToLiveTimeUnit(enumValueOf(environment.getProperty("feign.httpclient.time-to-live-unit", "seconds").toUpperCase()))
                            .followRedirects(java.lang.Boolean.valueOf(environment.getProperty("feign.httpclient.follow-redirects", "true")))
                            .disableSslValidation(java.lang.Boolean.valueOf(environment.getProperty("feign.httpclient.disable-ssl-validation", "false")))
                            .build().builder.build())
            if (clientProperties.requestReadTimeout < 0) {
                throw IllegalStateException("Invalid Value for Read Timeout set.")
            }
            requestFactory.setReadTimeout(clientProperties.requestReadTimeout)
            val template = RestTemplate(requestFactory)
            for (httpMessageConverter in template.messageConverters) {
                if (httpMessageConverter is MappingJackson2HttpMessageConverter) {
                    template.messageConverters.remove(httpMessageConverter)
                    template.messageConverters.add(MappingJackson2HttpMessageConverter(PackageTools.buildJacksonObjectMapper(environment)))
                    break
                }
            }
            val headers = clientProperties.headers.toMutableMap()
            headers.remove(AUTHORIZATION)
            if (headers.isNotEmpty()) {
                template.interceptors = listOf(ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor(headers))
            }
            log.info("Created ConfigClientRestTemplate For ACP")
            return template
        } catch (e: Exception) {
            log.error(e.message, e)
            return null
        }

    }
}