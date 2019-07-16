package pers.acp.spring.cloud.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.RemoteTokenServices
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import pers.acp.client.exceptions.HttpException
import pers.acp.client.http.HttpClientBuilder
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.cloud.constant.ConfigurationOrder
import pers.acp.spring.cloud.constant.RestPrefix

import java.util.ArrayList

/**
 * Oauth2 资源服务配置
 *
 * @author zhangbin by 11/04/2018 15:13
 * @since JDK 11
 */
@Configuration
@EnableResourceServer
@Order(ConfigurationOrder.resourceServerConfiguration)
class ResourceServerConfiguration @Autowired
constructor(private val acpOauthConfiguration: AcpOauthConfiguration,
            private val entryPointMap: Map<String, AuthenticationEntryPoint>,
            private val accessDeniedHandlerMap: Map<String, AccessDeniedHandler>,
            private val clientProperties: OAuth2ClientProperties,
            private val resourceServerProperties: ResourceServerProperties,
            private val feignHttpClientProperties: FeignHttpClientProperties,
            private val objectMapper: ObjectMapper,
            serverProperties: ServerProperties) : ResourceServerConfigurerAdapter() {

    private val log = LogFactory.getInstance(this.javaClass)

    private val contextPath: String = if (CommonTools.isNullStr(serverProperties.servlet.contextPath)) "" else serverProperties.servlet.contextPath

    /**
     * 获取服务配置信息
     *
     * @return ServerProperties
     */
    @Bean
    @ConditionalOnMissingBean(ServerProperties::class)
    fun serverProperties(): ServerProperties = ServerProperties()

    /**
     * oauth2 客户端配置
     *
     * @return OAuth2ClientProperties
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ClientProperties::class)
    fun oAuth2ClientProperties(): OAuth2ClientProperties = OAuth2ClientProperties()

    /**
     * 获取资源服务配置信息
     *
     * @return ResourceServerProperties
     */
    @Bean
    @ConditionalOnMissingBean(ResourceServerProperties::class)
    fun resourceServerProperties(): ResourceServerProperties = ResourceServerProperties()

    /**
     * feign客户端配置
     *
     * @return FeignHttpClientProperties
     */
    @Bean
    @ConditionalOnMissingBean(FeignHttpClientProperties::class)
    fun feignHttpClientProperties(): FeignHttpClientProperties = FeignHttpClientProperties()

    /**
     * 自定义负载均衡客户端
     *
     * @return RestTemplate
     * @throws HttpException 异常
     */
    @LoadBalanced
    @Bean("acpSpringCloudOauth2ClientRestTemplate")
    @Throws(HttpException::class)
    fun acpSpringCloudOauth2ClientRestTemplate(): RestTemplate =
            RestTemplate(OkHttp3ClientHttpRequestFactory(
                    HttpClientBuilder().maxTotalConn(feignHttpClientProperties.maxConnections)
                            .timeOut(feignHttpClientProperties.connectionTimeout)
                            .timeToLive(feignHttpClientProperties.timeToLive)
                            .timeToLiveTimeUnit(feignHttpClientProperties.timeToLiveUnit)
                            .followRedirects(feignHttpClientProperties.isFollowRedirects)
                            .disableSslValidation(feignHttpClientProperties.isDisableSslValidation)
                            .build().builder.build())).apply {
                this.messageConverters.add(MappingJackson2HttpMessageConverter(objectMapper))
            }

    /**
     * 自定义权限验证服务，远程调用认证服务进行验证
     *
     * @return 远程 token 认证服务实例
     */
    @Primary
    @Bean("acpResourceServerRemoteTokenServices")
    @ConditionalOnExpression("!'\${acp.cloud.oauth.oauth-server}'.equals('true')")
    fun remoteTokenServices(): RemoteTokenServices {
        val services = RemoteTokenServices()
        // 自定义错误处理类，所有错误放行统一交由 oauth 模块进行进出
        try {
            services.setRestTemplate(acpSpringCloudOauth2ClientRestTemplate().apply {
                this.errorHandler = object : ResponseErrorHandler {
                    override fun hasError(response: ClientHttpResponse): Boolean {
                        return false
                    }

                    override fun handleError(response: ClientHttpResponse) {

                    }
                }
            })
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        services.setCheckTokenEndpointUrl(resourceServerProperties.tokenInfoUri)
        services.setClientId(clientProperties.clientId)
        services.setClientSecret(clientProperties.clientSecret)
        return services
    }

    /**
     * 设置 token 验证服务
     * 设置自定义异常处理
     *
     * @param resources 资源服务安全验证配置对象
     */
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        if (!acpOauthConfiguration.isOauthServer) {
            resources.tokenServices(remoteTokenServices())
        }
        // 自定义 token 异常处理
        if (entryPointMap.isNotEmpty()) {
            if (entryPointMap.size > 1) {
                if (!CommonTools.isNullStr(acpOauthConfiguration.authExceptionEntryPoint)) {
                    resources.authenticationEntryPoint(entryPointMap[acpOauthConfiguration.authExceptionEntryPoint])
                } else {
                    log.warn("Find more than one authenticationEntryPoint, please specify explicitly in the configuration 'acp.cloud.auth.auth-exception-entry-point'")
                }
            } else {
                resources.authenticationEntryPoint(entryPointMap.entries.iterator().next().value)
            }
        }
        // 自定义权限异常处理
        if (accessDeniedHandlerMap.isNotEmpty()) {
            if (accessDeniedHandlerMap.size > 1) {
                if (!CommonTools.isNullStr(acpOauthConfiguration.accessDeniedHandler)) {
                    resources.accessDeniedHandler(accessDeniedHandlerMap[acpOauthConfiguration.accessDeniedHandler])
                } else {
                    log.warn("Find more than one accessDeniedHandler, please specify explicitly in the configuration 'acp.cloud.auth.access-denied-handler'")
                }
            } else {
                resources.accessDeniedHandler(accessDeniedHandlerMap.entries.iterator().next().value)
            }
        }
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val permitAll = ArrayList<String>()
        val security = ArrayList<String>()
        if (acpOauthConfiguration.isResourceServer) {
            log.info("resource server = true")
            permitAll.add("$contextPath/error")
            permitAll.add("$contextPath/actuator")
            permitAll.add("$contextPath/actuator/**")
            permitAll.add("$contextPath/v2/api-docs")
            permitAll.add("$contextPath/configuration/ui")
            permitAll.add("$contextPath/swagger-resources/**")
            permitAll.add("$contextPath/configuration/security")
            permitAll.add("$contextPath/swagger-ui.html")
            permitAll.add("$contextPath/webjars/**")
            permitAll.add("$contextPath/swagger-resources/configuration/ui")
            permitAll.add("$contextPath/hystrix.stream")
            permitAll.add("$contextPath/oauth/authorize")
            permitAll.add("$contextPath/oauth/token")
            permitAll.add("$contextPath/oauth/error")
            acpOauthConfiguration.resourceServerPermitAllPath.forEach { path -> permitAll.add(contextPath + path) }
            acpOauthConfiguration.resourceServerSecurityPath.forEach { path -> security.add(contextPath + path) }
            permitAll.add(contextPath + RestPrefix.Open + "/**")
        } else {
            log.info("resource server = false")
            permitAll.add("$contextPath/**")
        }
        permitAll.forEach { uri -> log.info("permitAll uri: $uri") }
        security.forEach { uri -> log.info("security uri: $uri") }
        log.info("security uri: other any")
        // match 匹配的url，赋予全部权限（不进行拦截）
        http.csrf().disable().authorizeRequests()
                .antMatchers(*security.toTypedArray()).authenticated()
                .antMatchers(*permitAll.toTypedArray()).permitAll()
                .anyRequest().authenticated()
                .and().formLogin().permitAll()
    }

}
