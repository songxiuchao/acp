package pers.acp.spring.cloud

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import pers.acp.spring.cloud.conf.*

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration
@EnableConfigurationProperties(
        AcoCloudLogServerConfiguration::class,
        AcpCloudLogServerClientConfiguration::class,
        AcpCloudOauthConfiguration::class)
@AutoConfigureAfter(OAuth2AutoConfiguration::class)
class AcpCloudPropertiesAutoConfiguration {

    /**
     * oauth2 客户端配置
     *
     * @return OAuth2ClientProperties
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(OAuth2ClientProperties::class)
    fun oAuth2ClientProperties(): OAuth2ClientProperties = OAuth2ClientProperties()

}
