package pers.acp.spring.cloud.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 鉴权相关配置
 *
 * @author zhang by 14/01/2019 17:30
 * @since JDK 11
 */
@Component
@ConfigurationProperties(prefix = "acp.cloud.oauth")
class AcpOauthConfiguration {

    /**
     * is oauth server
     * default false
     */
    var oauthServer = false

    /**
     * is resource server
     * default true
     */
    var resourceServer = true

    /**
     * customer auth exception entry point bean name
     */
    var authExceptionEntryPoint: String? = null

    /**
     * customer access denied handler
     */
    var accessDeniedHandler: String? = null

    /**
     * permitAll path, effective when resourceServer=true
     */
    var resourceServerPermitAllPath: MutableList<String> = mutableListOf()

    /**
     * security path, effective when resourceServer=true
     */
    var resourceServerSecurityPath: MutableList<String> = mutableListOf()

}
