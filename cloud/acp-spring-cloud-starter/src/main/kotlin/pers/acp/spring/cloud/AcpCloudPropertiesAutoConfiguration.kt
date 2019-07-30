package pers.acp.spring.cloud

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
class AcpCloudPropertiesAutoConfiguration
